package rpc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 23300
 * {@hide}
 */
public class RpcTools {
    public static final boolean DEBUG = true;
    /**
     * @param src
     * @param length
     * @return
     * {@hide}
     */
    public static String bytesToHexString(byte[] src,int length){  
        StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        length = src.length>length?length:src.length;
        for (int i = 0; i < length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    } 
    /** 
     * Convert hex string to byte[] 
     * @param hexString the hex string 
     * @return byte[] 
     * {@hide}
     */  
    public static byte[] hexStringToBytes(String hexString) {  
        if (hexString == null || hexString.equals("")) {  
            return null;  
        }  
        hexString = hexString.toUpperCase();  
        int length = hexString.length() / 2;  
        char[] hexChars = hexString.toCharArray();  
        byte[] d = new byte[length];  
        for (int i = 0; i < length; i++) {  
            int pos = i * 2;  
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
        }  
        return d;  
    }
    /** 
     * Convert char to byte 
     * @param c char 
     * @return byte 
     * {@hide}
     */  
     private static byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
     }
     
     public static int byte4ToIntUp(byte[] content,int offset){
         int value = 0;
         for(int i=0;i<4;i++){
             int shift = (3-i)*8;
             value+=(content[i+offset]& 0x000000ff)<<shift;
         }
         return value;
     }
     public static int byte4ToIntDown(byte[] content,int offset){
         int value = 0;
         for(int i=0;i<4;i++){
             int shift = i*8;
             value+=(content[i+offset]& 0x000000ff)<<shift;
         }
         return value;
     }
     public static void intToByte4Down(byte[] content,int offset,int value){
         for(int i=0;i<4;i++){
             int shift = i*8;
             content[i+offset] = (byte) (value>>shift & 0x000000ff);
         }
     }
     public static String[] parserMethod(String method){
         if(method == null){
             return null;
         }
         String[] methods = method.split("\\.");
         String[] retMethods = new String[2];
         retMethods[0] = methods[0];
         retMethods[1] = "";
         for(int i=1;i<methods.length;i++){
             retMethods[1] +=methods[i];
         }
          return retMethods;
     }
     public static String get32MD5(String src){
         try {
             MessageDigest md5 = MessageDigest.getInstance("MD5");
             md5.update(src.getBytes());
             byte[] byteDst = md5.digest();
             
             int i;
             StringBuffer buf = new StringBuffer("");
             for(int offset = 0; offset < byteDst.length; offset++){
                 i = byteDst[offset];
                 if(i < 0){
                     i+=256;
                 }
                 if(i < 16){
                     buf.append("0");
                 }
                 buf.append(Integer.toHexString(i).toUpperCase());
             }
             return buf.toString();
             
         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
             return null;
         }
     }
     
     public static String get16MD5(String src){
         try {
             MessageDigest md5 = MessageDigest.getInstance("MD5");
             md5.update(src.getBytes());
             byte[] byteDst = md5.digest();
             
             int i;
             StringBuffer buf = new StringBuffer("");
             for(int offset = 0; offset < byteDst.length; offset++){
                 i = byteDst[offset];
                 if(i < 0){
                     i+=256;
                 }
                 if(i < 16){
                     buf.append("0");
                 }
                 buf.append(Integer.toHexString(i));
             }
             return buf.toString().substring(8, 24);
             
         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
             return null;
         }
     }
     public static boolean isIPAddress(String ip){
         if(ip!=null && !ip.equals("")){
             String regularExpression = "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})){3}$";
             return ip.matches(regularExpression);
         }
         return false;
     }
}
