private void injectKeyIntoWebView(int keyCode) {

    String js =
        "javascript:(function() {" +

        // 🔥 Disable top panel focus completely
        "document.querySelectorAll('header *, #topbar *, .topbar *, nav *').forEach(function(el){" +
        "   el.setAttribute('tabindex','-1');" +
        "   el.blur();" +
        "});" +

        // 🔥 Disable sidebar focus
        "document.querySelectorAll('#sidebar *, #tree *, .sidebar *, .tree *').forEach(function(el){" +
        "   el.setAttribute('tabindex','-1');" +
        "   el.blur();" +
        "});" +

        // 🔥 Ensure at least one file item is focused
        "var focused = document.activeElement;" +
        "if(!focused || focused.tagName !== 'A') {" +
        "   var first = document.querySelector('.item a, tr td.name a');" +
        "   if(first) first.focus();" +
        "   focused = first;" +
        "}" +

        "if(!focused) return;" +

        "switch(" + keyCode + ") {" +

        // UP
        "case 19:" +
        "   var prevRow = focused.closest('tr')?.previousElementSibling;" +
        "   if(prevRow) prevRow.querySelector('a')?.focus();" +
        "   break;" +

        // DOWN
        "case 20:" +
        "   var nextRow = focused.closest('tr')?.nextElementSibling;" +
        "   if(nextRow) nextRow.querySelector('a')?.focus();" +
        "   break;" +

        // ENTER
        "case 23:" +
        "case 66:" +
        "   focused.click();" +
        "   break;" +

        "}" +

        "})();";

    webView.evaluateJavascript(js, null);
}
