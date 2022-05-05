package com.zhs.communication.usbserial.example.canopen.sdo

class SdoAbortedError constructor(var errorCode:Int){

    private val errorCodeMap = mapOf<Int,String > (
        0x05030000 to "SDO toggle bit error",
        0x05040000 to "Timeout of transfer communication detected",
        0x05040001 to "Unknown SDO command specified",
        0x05040002 to "Invalid block size",
        0x05040003 to "Invalid sequence number",
        0x05040004 to "CRC error",
        0x05040005 to "Out of memory",
        0x06010000 to "Unsupported access to an object",
        0x06010001 to "Attempt to read a write only object",
        0x06010002 to "Attempt to write a read only object",
        0x06020000 to "Object does not exist",
        0x06040041 to "Object cannot be mapped to the PDO",
        0x06040042 to "PDO length exceeded",
        0x06040043 to "General parameter incompatibility reason",
        0x06040047 to "General internal incompatibility in the device",
        0x06060000 to "Access failed due to a hardware error",
        0x06070010 to "Data type and length code do not match",
        0x06070012 to "Data type does not match, length of service parameter too high",
        0x06070013 to "Data type does not match, length of service parameter too low",
        0x06090011 to "Subindex does not exist",
        0x06090030 to "Value range of parameter exceeded",
        0x06090031 to "Value of parameter written too high",
        0x06090032 to "Value of parameter written too low",
        0x06090036 to "Maximum value is less than minimum value",
        0x060A0023 to "Resource not available",
        0x08000000 to "General error",
        0x08000020 to "Data cannot be transferred or stored to the application",
        0x08000021 to ("Data can not be transferred or stored to the application because of local control"),
        0x08000022 to ("Data can not be transferred or stored to the application because of the present device state"),
        0x08000023 to ("Object dictionary dynamic generation fails or no object dictionary is present"),
        0x08000024 to "No data available",
    )

    fun getErrorCodeString():String{
        var text:String = "Code 0x"+ errorCode.toString(16)
        text = if(errorCode in errorCodeMap){
            text+" " +errorCodeMap[errorCode]
        } else{
            "$text no code define "
        }
        return text

    }
}