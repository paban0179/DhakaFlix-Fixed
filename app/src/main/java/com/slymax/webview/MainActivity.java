@Override
public boolean dispatchKeyEvent(KeyEvent event) {

    if (event.getAction() == KeyEvent.ACTION_UP) {

        int code = event.getKeyCode();

        if (code == KeyEvent.KEYCODE_DPAD_UP ||
            code == KeyEvent.KEYCODE_DPAD_DOWN ||
            code == KeyEvent.KEYCODE_DPAD_CENTER ||
            code == KeyEvent.KEYCODE_ENTER) {

            String js =
                    "javascript:(function() {" +

                    "var focused=document.activeElement;" +

                    // If focus escaped to header/sidebar, force back to list
                    "if(!focused || focused.closest('#topbar, header, nav, #sidebar, .sidebar')){" +
                    "var first=document.querySelector('.item a, tr td.name a');" +
                    "if(first) first.focus();" +
                    "focused=first;" +
                    "}" +

                    "if(!focused) return;" +

                    "switch(" + code + ") {" +

                    // UP
                    "case 19:" +
                    "var prev=focused.closest('tr')?.previousElementSibling;" +
                    "if(prev) prev.querySelector('a')?.focus();" +
                    "break;" +

                    // DOWN
                    "case 20:" +
                    "var next=focused.closest('tr')?.nextElementSibling;" +
                    "if(next) next.querySelector('a')?.focus();" +
                    "break;" +

                    // ENTER
                    "case 23:" +
                    "case 66:" +
                    "if(focused.tagName==='A') focused.click();" +
                    "break;" +

                    "}" +

                    "})();";

            webView.evaluateJavascript(js, null);
            return true;
        }
    }

    return super.dispatchKeyEvent(event);
}
