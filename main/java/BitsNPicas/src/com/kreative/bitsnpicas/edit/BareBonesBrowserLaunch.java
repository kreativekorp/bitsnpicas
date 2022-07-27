package com.kreative.bitsnpicas.edit;

///////////////////////////////
// Bare Bones Browser Launch //
///////////////////////////////

import javax.swing.JOptionPane;
import java.util.Arrays;

/**
 * Utility class to open a web page from a Swing application
 * in the user's default browser.
 * <p>
 * Supports: Mac OS X, Linux, Unix, Windows
 * <p>
 * Example usage:<br><code>&nbsp;&nbsp;
 *    String url = "https://dnajs.org/";<br>&nbsp;&nbsp;
 *    BareBonesBrowserLaunch.openURL(url);</code>
 * <p>
 * Latest Version: <a href=https://centerkey.com/java/browser>
 *    https://centerkey.com/java/browser</a>
 * <p>
 * Published: October 24, 2010
 * <p>
 * License: WTFPL
 *
 * @author  Dem Pilafian
 * @version 3.2
 */
public class BareBonesBrowserLaunch {

   static final String[] browsers = { "x-www-browser", "google-chrome",
      "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori",
      "kazehakase", "mozilla" };
   static final String errMsg = "Error attempting to launch web browser";

   /**
    * Open the specified web page in the user's default browser
    * @param url A web address (URL) of a web page (example: <code>"https://dnajs.org/"</code>)
    */
   public static void openURL(String url) {
      try {  //attempt to use Desktop library from JDK 1.6+
         Class<?> d = Class.forName("java.awt.Desktop");
         d.getDeclaredMethod("browse",
            new Class<?>[] {java.net.URI.class}).invoke(
               d.getDeclaredMethod("getDesktop").invoke(null),
               new Object[] {java.net.URI.create(url)});
         }
      catch (Exception ignore) {  //library not available or failed
         String osName = System.getProperty("os.name");
         try {
            if (osName.startsWith("Mac OS")) {
               Class.forName("com.apple.eio.FileManager").getDeclaredMethod(
                  "openURL", new Class<?>[] {String.class}).invoke(null,
                  new Object[] {url});
               }
            else if (osName.startsWith("Windows"))
               Runtime.getRuntime().exec(
                  "rundll32 url.dll,FileProtocolHandler " + url);
            else { //assume Unix or Linux
               String browser = null;
               for (String b : browsers)
                  if (browser == null && Runtime.getRuntime().exec(new String[]
                        {"which", b}).getInputStream().read() != -1)
                     Runtime.getRuntime().exec(new String[] {browser = b, url});
               if (browser == null)
                  throw new Exception(Arrays.toString(browsers));
               }
            }
         catch (Exception e) {
            JOptionPane.showMessageDialog(null, errMsg + "\n" + e.toString());
            }
         }
      }

   }