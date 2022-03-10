package org.techdown.photogallery

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

private const val ARG_URI="photo_page_url"
class PhotoPageFragment:VisibleFragment(){
    private lateinit var uri:Uri
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        uri=arguments?.getParcelable(ARG_URI)?:Uri.EMPTY

    }


    @Suppress("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_photo_page,container,false)

        progressBar=view.findViewById(R.id.progress_bar)
        progressBar.max=100

        webView=view.findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled=true // javascript 사용 활성화
        webView.webChromeClient=object:WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if(newProgress==100){
                    progressBar.visibility=View.GONE
                }
                else{
                    progressBar.visibility=View.VISIBLE
                    progressBar.progress=newProgress
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                (activity as AppCompatActivity).supportActionBar?.subtitle=title
            }
        }
        webView.webViewClient= WebViewClient()
        webView.loadUrl(uri.toString())
        return view
    }
    fun getBackinfoOfWebView():Boolean{
        return webView.canGoBack()
    }
    fun goBackWebView(){
        webView.goBack()
    }

    companion object{
        fun newInstance(uri:Uri):PhotoPageFragment{
            return PhotoPageFragment().apply{
                arguments=Bundle().apply{
                    putParcelable(ARG_URI,uri)
                }
            }
        }
    }
}