package ioOperation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
 
public class PropertiesCache
{
   private final Properties configProp = new Properties();
    
   private PropertiesCache()
   {
      //Private constructor to restrict new instances
      InputStream in = this.getClass().getClassLoader().getResourceAsStream("resources/app.properties");
      System.out.println("Read all properties from file");
      try {
          configProp.load(in);
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
 
   //Bill Pugh Solution for singleton pattern
   private static class LazyHolder
   {
      private static final PropertiesCache INSTANCE = new PropertiesCache();
   }
 
   public static PropertiesCache getInstance()
   {
      return LazyHolder.INSTANCE;
   }
    
   public String getPropertyString(String key){
      return configProp.getProperty(key);
   }
   
   public int getPropertyInt(String key){
	      return Integer.parseInt(configProp.getProperty(key));
	   }
    
   public Set<String> getAllPropertyNames(){
      return configProp.stringPropertyNames();
   }
    
   public boolean containsKey(String key){
      return configProp.containsKey(key);
   }
   
   public static void main(String[] args)
   {  
	   
	   int foo = Integer.parseInt("1234");
     //Get individual properties
     System.out.println(PropertiesCache.getInstance().getPropertyString("COAP_SECURE_PORT"));
     System.out.println(PropertiesCache.getInstance().getPropertyString("KAFKA_BROKER1_ADD"));
      
     //All property names
     System.out.println(PropertiesCache.getInstance().getAllPropertyNames());
   }
}