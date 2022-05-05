package com.zhs.communication.usbserial.example.canopen.sdo


const val REQUEST_SEGMENT_DOWNLOAD = 0 shl 5
const val REQUEST_DOWNLOAD = 1 shl 5
const val REQUEST_UPLOAD = 2 shl 5
const val REQUEST_SEGMENT_UPLOAD = 3 shl 5
const val REQUEST_ABORTED = 4 shl 5
const val REQUEST_BLOCK_UPLOAD = 5 shl 5
const val REQUEST_BLOCK_DOWNLOAD = 6 shl 5

const val RESPONSE_SEGMENT_UPLOAD = 0 shl 5
const val RESPONSE_SEGMENT_DOWNLOAD = 1 shl 5
const val RESPONSE_UPLOAD = 2 shl 5
const val RESPONSE_DOWNLOAD = 3 shl 5
const val RESPONSE_ABORTED = 4 shl 5
const val RESPONSE_BLOCK_DOWNLOAD = 5 shl 5
const val RESPONSE_BLOCK_UPLOAD = 6 shl 5

const val INITIATE_BLOCK_TRANSFER = 0
const val END_BLOCK_TRANSFER = 1
const val BLOCK_TRANSFER_RESPONSE = 2
const val START_BLOCK_UPLOAD = 3

const val EXPEDITED = 0x2
const val SIZE_SPECIFIED = 0x1
const val BLOCK_SIZE_SPECIFIED = 0x2
const val CRC_SUPPORTED = 0x4
const val NO_MORE_DATA = 0x1
const val NO_MORE_BLOCKS = 0x80
const val TOGGLE_BIT = 0x10