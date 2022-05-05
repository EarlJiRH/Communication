package com.example.myapplication.control_my.common

import com.zhs.communication.controller.common.FoodCart
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class JsonUtil {

    companion object {


        fun get_key_string(key:String,jsonString:String):String {
            var str: String = ""

            try {

                val jsonObj: JSONObject = JSONObject(jsonString)
                str=jsonObj.getString(key)


            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return str
        }

        fun get_key_boolean(key:String,jsonString:String):Boolean {
            var str: Boolean =true
            try {
                val jsonObj:JSONObject= JSONObject(jsonString)
                str=jsonObj.getBoolean(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return str
        }

        fun get_key_int(key:String,jsonString:String):Int {
            var str: Int=0
            try {
                val jsonObj:JSONObject= JSONObject(jsonString)
                str=jsonObj.getInt(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return str
        }


        fun getList(key: String, jsonString: String): List<String> {
            val list = ArrayList<String>()
            try {
                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray(key)
                for (i in 0 until jsonArray.length()) {
                    val msg = jsonArray.getString(i)
                    list.add(msg)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return list
        }


        fun getListMap(key: String, jsonString: String): List<Map<String, Any>> {
            val list = ArrayList<Map<String, Any>>()
            try {
                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray(key)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject2 = jsonArray.getJSONObject(i)
                    val map = HashMap<String, Any>()
                    val iterator = jsonObject2.keys()
                    while (iterator.hasNext()) {
                        val json_key = iterator.next()
                        var json_value: Any? = jsonObject2.get(json_key)
                        if (json_value == null) {
                            json_value = ""
                        }
                        map[json_key] = json_value
                    }
                    list.add(map)
                }
            } catch (e: JSONException) {

                e.printStackTrace()
            }
            return list
        }


    }

}

class mymqttconfigget{

    companion object{
        var mqttjsonconnt :String = ""
        var dev_id:String = ""
    }

    var use_env:String = ""

    var cart_type:Int = 0

    var formal_env_ip:String = ""
    var formal_env_port:Int = 0
    var formal_env_user_name:String = ""
    var formal_env_password:String = ""

    var test_env_ip:String = ""
    var test_env_port:Int = 0
    var test_env_user_name:String = ""
    var test_env_password:String = ""



    fun savedevid2(devid:String):Boolean{
        println("devid=[${devid}]length=${devid.length}")
        if (devid.length==16)
        {
            val devidfilemy: File = File(FoodCart.mqttConfigPath)
            devidfilemy.writeText("{\n" +
                    "  \"test_env\": {\n" +
                    "    \"ip\": \"www.jiutongtang.net\",\n" +
                    "    \"port\": 2883,\n" +
                    "    \"user_name\": \"\",\n" +
                    "    \"password\": \"\"\n" +
                    "  },\n" +
                    "  \"formal_env\": {\n" +
                    "    \"ip\": \"zhskg.net\",\n" +
                    "    \"port\": 11883,\n" +
                    "    \"user_name\": \"smart_car\",\n" +
                    "    \"password\": \"zhskg@2020\"\n" +
                    "  },\n" +
                    "\n" +
                    "  \"use_env\":\"formal_env\",\n" +
                    "  \"cart_type\":4,\n" +
                    "  \"dev_id\":\"${devid}\"\n" +
                    "}")

            return true

        }
        return false
    }

    fun getloclconfigt(){
//        var mqttjsonconn = readAssetsTxt(,"mqttconfig.json")



        try {
            dev_id = JsonUtil.get_key_string("dev_id", mqttjsonconnt)
            use_env = JsonUtil.get_key_string("use_env", mqttjsonconnt)
            cart_type = JsonUtil.get_key_int("cart_type", mqttjsonconnt)

            val formatenvt = JsonUtil.get_key_string("formal_env", mqttjsonconnt)
            if (formatenvt!=""){
                formal_env_ip= JsonUtil.get_key_string("ip", formatenvt)
                formal_env_port = JsonUtil.get_key_int("port", formatenvt)
                formal_env_user_name= JsonUtil.get_key_string("user_name", formatenvt)
                formal_env_password= JsonUtil.get_key_string("password", formatenvt)
            }

            val testenvt = JsonUtil.get_key_string("test_env", mqttjsonconnt)
            if (formatenvt!=""){
                test_env_ip= JsonUtil.get_key_string("ip", testenvt)
                test_env_port = JsonUtil.get_key_int("port", formatenvt)
                test_env_user_name= JsonUtil.get_key_string("user_name", testenvt)
                test_env_password= JsonUtil.get_key_string("password", testenvt)
            }

        }catch (e:Exception){
            println("$e")
        }

        println("use_env=${use_env}")
        println("dev_id=${dev_id}")
        println("cart_type=${cart_type}")
        println("formal_env: ip=${formal_env_ip} port=${formal_env_port} username=${formal_env_user_name} password=${formal_env_password}")
        println("test_env: ip=${test_env_ip} port=${test_env_port} username=${test_env_user_name} password=${test_env_password}")
    }

}