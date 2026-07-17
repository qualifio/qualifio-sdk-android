package com.qualifio.views

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.qualifio.Qualifio
import com.qualifio.R
import com.qualifio.internal.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Inline Android view for loading a Qualifio campaign.
 */
public class QualifioInlineCampaignView private constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    campaign: String?,
    autoScrollOnPageLoadEnabledOverride: Boolean?,
) : FrameLayout(context, attrs, defStyleAttr) {
    @JvmOverloads
    public constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.qualifioInlineCampaignViewStyle,
    ) : this(
        context = context,
        attrs = attrs,
        defStyleAttr = defStyleAttr,
        campaign = null,
        autoScrollOnPageLoadEnabledOverride = null,
    )

    /**
     * Creates an inline campaign view and loads provided [campaign].
     *
     * @param autoScrollOnPageLoadEnabled when true, subsequent campaign page loads request that
     * the parent scroll container bring this view back to its top edge.
     */
    public constructor(
        context: Context,
        campaign: String,
        autoScrollOnPageLoadEnabled: Boolean = true,
    ) : this(
        context = context,
        attrs = null,
        defStyleAttr = R.attr.qualifioInlineCampaignViewStyle,
        campaign = campaign,
        autoScrollOnPageLoadEnabledOverride = autoScrollOnPageLoadEnabled,
    )

    /**
     * When true, after the initial campaign page has loaded, each subsequent page load requests
     * that the parent scroll container bring this view back to its top edge.
     */
    public var autoScrollOnPageLoadEnabled: Boolean = true

    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var loadJob: Job? = null
    private var lastReportedHeightPx: Int = 0
    private var didLoadInitialPage: Boolean = false

    private val webView: WebView = WebView(context)

    private val inlineWebViewClient: WebViewClient =
        object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError,
            ) {
                if (request.isForMainFrame) {
                    logger.error("Failed to load inline campaign: ${error.description}")
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                reportContentHeightFromDocument()

                if (!didLoadInitialPage) {
                    didLoadInitialPage = true
                    return
                }

                if (autoScrollOnPageLoadEnabled) {
                    scrollContainingScrollViewToTop()
                }
            }
        }

    init {
        val initialConfiguration = resolveInitialConfiguration(
            context = context,
            attrs = attrs,
            defStyleAttr = defStyleAttr,
            campaign = campaign,
            autoScrollOnPageLoadEnabledOverride = autoScrollOnPageLoadEnabledOverride,
        )

        autoScrollOnPageLoadEnabled = initialConfiguration.autoScrollOnPageLoadEnabled

        setupWebView()

        if (initialConfiguration.campaign == null) {
            loadCampaign()
        } else {
            loadCampaign(initialConfiguration.campaign)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        webView.onResume()
    }

    override fun onDetachedFromWindow() {
        webView.onPause()

        super.onDetachedFromWindow()
    }

    /**
     * Loads a campaign into the inline WebView.
     */
    @JvmOverloads
    public fun loadCampaign(campaign: String = Qualifio.DEFAULT_CAMPAIGN) {
        val configuration = Qualifio.configuration
            ?: throw IllegalStateException("No configuration set. Please call Qualifio.configure()")

        if (campaign.isBlank()) {
            throw IllegalArgumentException("Invalid campaign parameter.")
        }

        loadJob?.cancel()
        loadJob = viewScope.launch {
            val url = try {
                Qualifio.prepareCampaignUrl(configuration, campaign)
            } catch (e: Exception) {
                logger.error("Failed to prepare the campaign URL.", e)
                return@launch
            }

            didLoadInitialPage = false
            webView.loadUrl(url.toString())
        }
    }

    private fun resolveInitialConfiguration(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        campaign: String?,
        autoScrollOnPageLoadEnabledOverride: Boolean?,
    ): InitialConfiguration {
        val attributes = resolveQualifioAttributes(context, attrs, defStyleAttr)

        return InitialConfiguration(
            campaign = campaign
                ?.takeIf { it.isNotBlank() }
                ?: attributes.campaign,
            autoScrollOnPageLoadEnabled = autoScrollOnPageLoadEnabledOverride
                ?: attributes.autoScrollOnPageLoadEnabled,
        )
    }

    private fun resolveQualifioAttributes(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
    ): InitialConfiguration {
        var campaign: String? = null
        var autoScrollOnPageLoadEnabled = autoScrollOnPageLoadEnabled

        context.withStyledAttributes(
            attrs,
            R.styleable.QualifioInlineCampaignView,
            defStyleAttr,
            0,
        ) {
            autoScrollOnPageLoadEnabled = getBoolean(
                R.styleable.QualifioInlineCampaignView_qualifioAutoScrollOnPageLoadEnabled,
                autoScrollOnPageLoadEnabled,
            )

            getString(R.styleable.QualifioInlineCampaignView_qualifioCampaign)
                ?.takeIf { it.isNotBlank() }
                ?.let { campaignValue ->
                    campaign = campaignValue
                }
        }

        return InitialConfiguration(
            campaign = campaign,
            autoScrollOnPageLoadEnabled = autoScrollOnPageLoadEnabled,
        )
    }

    private fun setupWebView() {
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(ContentHeightBridge(), CONTENT_HEIGHT_BRIDGE_NAME)
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.webViewClient = inlineWebViewClient

        addView(
            webView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private fun reportContentHeight(heightCssPx: Float) {
        val heightPx = ceil(heightCssPx * resources.displayMetrics.density)
            .roundToInt()

        if (heightPx <= 0 || abs(heightPx - lastReportedHeightPx) < 1) {
            return
        }

        lastReportedHeightPx = heightPx
        updateLayoutHeight(heightPx)
    }

    private fun updateLayoutHeight(heightPx: Int) {
        layoutParams?.let { currentLayoutParams ->
            if (currentLayoutParams.height != heightPx) {
                currentLayoutParams.height = heightPx
                layoutParams = currentLayoutParams
            }
        }
    }

    private fun scrollContainingScrollViewToTop() {
        post {
            requestRectangleOnScreen(Rect(0, 0, 1, 1), false)
        }
    }

    private fun reportContentHeightFromDocument() {
        webView.evaluateJavascript(CONTENT_HEIGHT_SCRIPT, null)
    }

    private inner class ContentHeightBridge {
        @JavascriptInterface
        fun postContentHeight(height: String) {
            val parsedHeight = height.toFloatOrNull() ?: return

            webView.post {
                reportContentHeight(parsedHeight)
            }
        }
    }

    private data class InitialConfiguration(
        val campaign: String?,
        val autoScrollOnPageLoadEnabled: Boolean,
    )

    private companion object {
        private const val CONTENT_HEIGHT_BRIDGE_NAME = "QualifioInlineCampaignView"
        private const val CONTENT_HEIGHT_SCRIPT = """
            (function() {
              if (window.__qualifioInlineCampaignViewHeightObserver) {
                window.__qualifioInlineCampaignViewHeightObserver.report();
                return;
              }

              var measure = function() {
                var body = document.body;
                var scrollY = window.pageYOffset || 0;
                var height = 0;

                if (body) {
                  var bodyRect = body.getBoundingClientRect();
                  height = Math.max(height, bodyRect.bottom + scrollY);

                  var elements = body.querySelectorAll('*');
                  for (var i = 0; i < elements.length; i += 1) {
                    var rects = elements[i].getClientRects();
                    for (var j = 0; j < rects.length; j += 1) {
                      height = Math.max(height, rects[j].bottom + scrollY);
                    }
                  }
                }

                if (height > 0) {
                  return Math.ceil(height);
                }

                var documentElement = document.documentElement;
                return Math.ceil(Math.max(
                  body ? body.scrollHeight : 0,
                  documentElement ? documentElement.scrollHeight : 0,
                  body ? body.offsetHeight : 0,
                  documentElement ? documentElement.offsetHeight : 0
                ));
              };

              var lastHeight = 0;
              var report = function() {
                window.requestAnimationFrame(function() {
                  var height = measure();

                  if (Math.abs(height - lastHeight) < 1) {
                    return;
                  }

                  lastHeight = height;
                  if (window.QualifioInlineCampaignView && window.QualifioInlineCampaignView.postContentHeight) {
                    window.QualifioInlineCampaignView.postContentHeight(height.toString());
                  }
                });
              };

              window.__qualifioInlineCampaignViewHeightObserver = { report: report };

              if (window.ResizeObserver) {
                var resizeObserver = new ResizeObserver(report);
                resizeObserver.observe(document.documentElement);
                if (document.body) {
                  resizeObserver.observe(document.body);
                }
              }

              if (window.MutationObserver) {
                var mutationObserver = new MutationObserver(report);
                mutationObserver.observe(document.documentElement, {
                  attributes: true,
                  childList: true,
                  subtree: true
                });
              }

              window.addEventListener('load', report);
              window.addEventListener('resize', report);
              report();
            })();
        """
    }
}
