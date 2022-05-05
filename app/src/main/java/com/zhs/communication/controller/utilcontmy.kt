package com.zhs.communication.controller


class utilcontmy {
    companion object{

        fun zeros(shapesize:Int, dtype:String="int16"): IntArray {
//            if (dtype=="long"){
//                return LongArray(shapesize)
//            }
//            else{
                return IntArray(shapesize)
//            }
        }
        fun zeros(shapesize0:Int,shapesize1:Int,shapesize2:Int,dtype:String="int16"): Array<Array<IntArray>> {

            return Array(shapesize0){Array(shapesize1){IntArray(shapesize2)}}

        }
        fun zeros(shapesize0:Int,shapesize1:Int,dtype:String="int16"): Array<IntArray>{

            return Array(shapesize0){IntArray(shapesize1)}

        }
    }


}

class time{
    companion object{

        fun time(): Long {
            //返回当前毫秒
            return System.currentTimeMillis()
        }

        fun sleep(sleepms:Long){
            Thread.sleep(sleepms)
        }

    }
}