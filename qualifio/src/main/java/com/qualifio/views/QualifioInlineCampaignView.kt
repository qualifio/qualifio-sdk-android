package com.qualifio.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.doOnLayout
import com.qualifio.Qualifio
import com.qualifio.R
import com.qualifio.internal.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
     * Creates an inline campaign view and loads the provided [campaign].
     *
     * @param autoScrollOnPageLoadEnabled when true, subsequent campaign page loads request that
     * the parent scroll container bring this view back to its top edge.
     */
    @JvmOverloads
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

    private val webView: WebView = WebView(context)
    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var loadJob: Job? = null
    private var currentLoadRequestId: Int = 0
    private var lastReportedHeightPx: Int = 0
    private var acceptsContentHeightReports: Boolean = false
    private var didLoadInitialPage: Boolean = false

    private val loadingViewportHeightPx: Int
        get() = (200 * resources.displayMetrics.density).roundToInt()

    private val inlineWebViewClient: WebViewClient =
        object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
            ): Boolean {
                if (!request.isForMainFrame) {
                    return false
                }

                val scheme = request.url.scheme?.lowercase()
                if (scheme == "http" || scheme == "https") {
                    return false
                }

                // The WebView can't load mailto:, tel:, etc. Let the OS handle them.
                openExternalUrl(request.url)
                return true
            }

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
                acceptsContentHeightReports = true
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

        updateLayoutHeight(lastReportedHeightPx)
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
        val configuration = Qualifio.configuration ?: run {
            logger.error("No configuration set. Please call Qualifio.configure()")
            return
        }

        if (campaign.isBlank()) {
            logger.error("Invalid campaign parameter.")
            return
        }

        val loadRequestId = ++currentLoadRequestId
        loadJob?.cancel()
        lastReportedHeightPx = 0
        acceptsContentHeightReports = false
        applyLoadingLayout()

        loadJob = viewScope.launch {
            val url = try {
                Qualifio.prepareCampaignUrl(configuration, campaign)
            } catch (e: Exception) {
                logger.error("Failed to prepare the campaign URL.", e)
                return@launch
            }

            webView.doOnLayout {
                if (loadRequestId != currentLoadRequestId) {
                    return@doOnLayout
                }

                didLoadInitialPage = false
                webView.loadUrl(url.toString())
            }
        }
    }

    private fun resolveInitialConfiguration(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        campaign: String?,
        autoScrollOnPageLoadEnabledOverride: Boolean?,
    ): InlineCampaignConfiguration {
        val attributes = resolveConfigurationFromAttributes(context, attrs, defStyleAttr)

        return InlineCampaignConfiguration(
            campaign = campaign
                ?: attributes.campaign,
            autoScrollOnPageLoadEnabled = autoScrollOnPageLoadEnabledOverride
                ?: attributes.autoScrollOnPageLoadEnabled,
        )
    }

    private fun resolveConfigurationFromAttributes(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
    ): InlineCampaignConfiguration {
        var campaign: String? = null
        var autoScrollOnPageLoadEnabled = autoScrollOnPageLoadEnabled

        context.withStyledAttributes(
            attrs,
            R.styleable.QualifioInlineCampaignView,
            defStyleAttr
        ) {
            autoScrollOnPageLoadEnabled = getBoolean(
                R.styleable.QualifioInlineCampaignView_qualifioAutoScrollOnPageLoadEnabled,
                autoScrollOnPageLoadEnabled,
            )

            campaign = getString(R.styleable.QualifioInlineCampaignView_qualifioCampaign)
        }

        return InlineCampaignConfiguration(
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
                loadingViewportHeightPx,
            ),
        )
    }

    private fun applyLoadingLayout() {
        updateLayoutHeight(0)
        updateWebViewHeight(loadingViewportHeightPx)
    }

    private fun reportContentHeight(heightCssPx: Float) {
        val density = resources.displayMetrics.density
        val heightPx = ceil(heightCssPx * density).roundToInt()

        if (heightPx < 1) {
            return
        }

        if (heightPx == lastReportedHeightPx) {
            return
        }

        applyContentHeight(heightPx)
    }

    private fun applyContentHeight(heightPx: Int) {
        lastReportedHeightPx = heightPx
        updateWebViewHeight(heightPx)
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

    private fun updateWebViewHeight(heightPx: Int) {
        webView.layoutParams?.let { currentLayoutParams ->
            if (currentLayoutParams.height != heightPx) {
                currentLayoutParams.height = heightPx
                webView.layoutParams = currentLayoutParams
            }
        }
    }

    private fun openExternalUrl(url: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, url)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            logger.warning("Failed to open the ${url.scheme} link.", e)
        }
    }

    private fun scrollContainingScrollViewToTop() {
        post {
            requestRectangleOnScreen(Rect(0, 0, 1, 1), false)
        }
    }

    private fun reportContentHeightFromDocument() {
        webView.evaluateJavascript(
            "$CONTENT_HEIGHT_SCRIPT();",
            null,
        )
    }

    private inner class ContentHeightBridge {
        @JavascriptInterface
        fun postContentHeight(heightString: String) {
            val height = heightString.toFloatOrNull() ?: return

            webView.post {
                if (!acceptsContentHeightReports) {
                    return@post
                }

                reportContentHeight(height)
            }
        }
    }

    private data class InlineCampaignConfiguration(
        val campaign: String?,
        val autoScrollOnPageLoadEnabled: Boolean,
    )

    private companion object {
        private const val CONTENT_HEIGHT_BRIDGE_NAME = "QualifioInlineCampaignView"
        private const val CONTENT_HEIGHT_SCRIPT = """
            (function() {
              if (window.__qualifioInlineCampaignHeightObserver) {
                window.__qualifioInlineCampaignHeightObserver.report();
                return;
              }

              var measureDocument = function(
                targetDocument,
                targetWindow,
                correctIframeHeight
              ) {
                var body = targetDocument.body;
                var scrollY = targetWindow ? targetWindow.pageYOffset || 0 : 0;
                var height = 0;

                if (body) {
                  var elements = body.querySelectorAll('*');
                  for (var i = 0; i < elements.length; i += 1) {
                    height = Math.max(
                      height,
                      elements[i].getBoundingClientRect().bottom + scrollY
                    );
                  }
                }

                if (height <= 0) {
                  var documentElement = targetDocument.documentElement;
                  height = Math.max(
                    body ? body.scrollHeight : 0,
                    documentElement ? documentElement.scrollHeight : 0,
                    body ? body.offsetHeight : 0,
                    documentElement ? documentElement.offsetHeight : 0
                  );
                }

                if (correctIframeHeight && body) {
                  var iframes = body.querySelectorAll('iframe');
                  if (iframes.length === 1) {
                    var iframe = iframes[0];
                    var hasDeclaredHeight =
                      iframe.style.height !== '' || iframe.hasAttribute('height');
                    if (hasDeclaredHeight) {
                      try {
                        var iframeDocument = iframe.contentDocument;
                        if (iframeDocument) {
                          if (
                            !iframeDocument.body ||
                            iframeDocument.body.children.length === 0
                          ) {
                            return 0;
                          }

                          var contentHeight = measureDocument(
                            iframeDocument,
                            iframe.contentWindow,
                            false
                          );
                          if (contentHeight <= 0) {
                            return 0;
                          }

                          height +=
                            contentHeight - iframe.getBoundingClientRect().height;
                        }
                      } catch (error) {
                        // Cross-origin frames keep their rendered outer size.
                      }
                    }
                  }
                }

                return Math.ceil(height);
              };

              var lastHeight = 0;
              var reportScheduled = false;
              var postContentHeight = function(height) {
                if (height === lastHeight) {
                  return;
                }

                lastHeight = height;
                if (window.QualifioInlineCampaignView && window.QualifioInlineCampaignView.postContentHeight) {
                  window.QualifioInlineCampaignView.postContentHeight(height.toString());
                }
              };

              var report = function() {
                if (reportScheduled) {
                  return;
                }

                reportScheduled = true;
                window.requestAnimationFrame(function() {
                  reportScheduled = false;
                  postContentHeight(measureDocument(document, window, true));
                });
              };

              window.__qualifioInlineCampaignHeightObserver = { report: report };

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

              var iframes = document.querySelectorAll('iframe');
              for (var i = 0; i < iframes.length; i += 1) {
                iframes[i].addEventListener('load', report);
              }

              report();
            })
        """
    }
}
