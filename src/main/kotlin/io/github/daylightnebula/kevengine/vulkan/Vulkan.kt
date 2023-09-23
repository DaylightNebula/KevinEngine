package io.github.daylightnebula.kevengine.vulkan

//import org.lwjgl.PointerBuffer
//import org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions
//import org.lwjgl.system.MemoryStack
//import org.lwjgl.system.MemoryUtil
//import org.lwjgl.vulkan.*
//import org.lwjgl.vulkan.EXTDebugUtils.*
//import org.lwjgl.vulkan.VK10.*
//import org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1
//
//
//private fun getSupportedValidationLayers() = MemoryStack.stackPush().use { stack ->
//    // get number of layers
//    val numLayersArr = stack.callocInt(1)
//    vkEnumerateInstanceLayerProperties(numLayersArr, null)
//    val numLayers = numLayersArr.get(0)
//    println("Instance supports $numLayers layers")
//
//    // get list of supported layers
//    val propsBuffer = VkLayerProperties.calloc(numLayers, stack)
//    vkEnumerateInstanceLayerProperties(numLayersArr, propsBuffer)
//    val supportedLayers = mutableListOf<String>()
//    repeat(numLayers) { idx ->
//        val props = propsBuffer.get(idx)
//        val layerName = props.layerNameString()
//        supportedLayers.add(layerName)
//        println("- Supported Layer: $layerName")
//    }
//
//    // main validation layer
//    if (supportedLayers.contains("VK_LAYER_KHRONOS_validation")) return@use listOf<String>("VK_LAYER_KHRONOS_validation")
//
//    // attempt to use lun arg validation layer
//    if (supportedLayers.contains("VK_LAYER_LUNARG_standard_validation")) return@use listOf("VK_LAYER_LUNARG_standard_validation")
//
//    // nuclear option
//    return@use listOf(
//        "VK_LAYER_GOOGLE_threading",
//        "VK_LAYER_LUNARG_parameter_validation",
//        "VK_LAYER_LUNARG_object_tracker",
//        "VK_LAYER_LUNARG_core_validation",
//        "VK_LAYER_GOOGLE_unique_objects"
//    ).filter(supportedLayers::contains)
//}
//
//private fun getInstanceExtensions() = MemoryStack.stackPush().use { stack ->
//    val instanceExtensions = mutableSetOf<String>()
//
//    // get number of extensions
//    val numExtBuffer = stack.callocInt(1)
//    vkEnumerateInstanceExtensionProperties(null as String?, numExtBuffer, null)
//    val numExtensions = numExtBuffer.get(0)
//
//    // populate instance extensions set
//    val instanceExtensionsProps = VkExtensionProperties.calloc(numExtensions, stack)
//    vkEnumerateInstanceExtensionProperties(null as String?, numExtBuffer, instanceExtensionsProps)
//    repeat(numExtensions) { i ->
//        val props = instanceExtensionsProps.get(i)
//        val extensionName = props.extensionNameString()
//        instanceExtensions.add(extensionName)
//    }
//
//    instanceExtensions
//}
//
//const val MESSAGE_SEVERITY_BITMASK = VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT or
//        VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT
//const val MESSAGE_TYPE_BITMASK = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT or
//        VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT or
//        VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT
//
//private fun createDebugCallBack(): VkDebugUtilsMessengerCreateInfoEXT? {
//    return VkDebugUtilsMessengerCreateInfoEXT
//        .calloc()
//        .sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
//        .messageSeverity(MESSAGE_SEVERITY_BITMASK)
//        .messageType(MESSAGE_TYPE_BITMASK)
//        .pfnUserCallback { messageSeverity: Int, messageTypes: Int, pCallbackData: Long, pUserData: Long ->
//            val callbackData =
//                VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData)
//            if (messageSeverity and VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT != 0) {
//                println("VkDebugUtilsCallback, ${callbackData.pMessageString()}")
//            } else if (messageSeverity and VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT != 0) {
//                println("VkDebugUtilsCallback, ${callbackData.pMessageString()}")
//            } else if (messageSeverity and VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT != 0) {
//                error("VkDebugUtilsCallback, ${callbackData.pMessageString()}")
//            } else {
//                error("VkDebugUtilsCallback, ${callbackData.pMessageString()}")
//            }
//            VK_FALSE
//        }
//}
//
//fun vkCheck(err: Int, errMsg: String) {
//    if (err != VK_SUCCESS) {
//        throw java.lang.RuntimeException("$errMsg: $err")
//    }
//}
//
//lateinit var instance: VkInstance
//var vkDebugHandle = VK_NULL_HANDLE
//var debugUtils: VkDebugUtilsMessengerCreateInfoEXT? = null
//fun vulkanCreate(appName: String) = MemoryStack.stackPush().use { stack ->
//    // create new app info
//    val appInfo = VkApplicationInfo.calloc(stack)
//        .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
//        .pApplicationName(stack.UTF8(appName))
//        .applicationVersion(1)
//        .pEngineName(stack.UTF8("KevinEngine"))
//        .engineVersion(0)
//        .apiVersion(VK_API_VERSION_1_1)
//
//    // validation layers
//    val validationLayers = getSupportedValidationLayers()
//    var requiredLayers: PointerBuffer? = null
//    val supportsValidation = validationLayers.isNotEmpty()
//    if (supportsValidation) {
//        requiredLayers = stack.mallocPointer(validationLayers.size)
//        repeat(validationLayers.size) { idx ->
//            requiredLayers.put(idx, stack.ASCII(validationLayers[idx]))
//        }
//    }
//
//    // get supported extensions
//    val supportedExtensions = getInstanceExtensions()
//
//    // glfw extension
//    val glfwExtensions = glfwGetRequiredInstanceExtensions()
//        ?: throw RuntimeException("Failed to get GLFW platform surface extensions")
//
//    // setup debug utils
//    lateinit var extensions: PointerBuffer
//    val usePortability = false // sometimes true if on Mac
//    if (supportsValidation) {
//        val vkDebugUtilsExtension = stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME)
//        val numExtensions = if (usePortability) glfwExtensions.remaining() + 2 else glfwExtensions.remaining() + 1
//        extensions = stack.mallocPointer(numExtensions)
//        extensions.put(glfwExtensions).put(vkDebugUtilsExtension)
//        if (usePortability) extensions.put(stack.UTF8("VK_KHR_portability_enumeration"))
//    } else {
//        val numExtensions = if (usePortability) glfwExtensions.remaining() + 1 else glfwExtensions.remaining()
//        extensions = stack.mallocPointer(numExtensions)
//        extensions.put(glfwExtensions)
//        if (usePortability) extensions.put(stack.UTF8("VK_KHR_portability_enumeration"))
//    }
//    extensions.flip()
//
//    var extension = MemoryUtil.NULL
//    if (supportsValidation) {
//        debugUtils = createDebugCallBack()
//        extension = debugUtils?.address() ?: MemoryUtil.NULL
//    }
//
//    // create instance info
//    val instanceInfo = VkInstanceCreateInfo.calloc(stack)
//        .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
//        .pNext(extension)
//        .pApplicationInfo(appInfo)
//        .ppEnabledLayerNames(requiredLayers)
//        .ppEnabledExtensionNames(extensions)
//    if (usePortability) instanceInfo.flags(0x00000001)
//
//    // create final instance
//    val pInstance = stack.mallocPointer(1)
//    vkCheck(vkCreateInstance(instanceInfo, null, pInstance), "Error calling vk create instance!")
//    instance = VkInstance(pInstance.get(0), instanceInfo)
//
//    // enable debug utils
//    if (supportsValidation) {
//        val longBuff = stack.mallocLong(1)
//        vkCheck(vkCreateDebugUtilsMessengerEXT(instance, debugUtils!!, null, longBuff), "Error setting up debug utils!")
//        vkDebugHandle = longBuff.get(0)
//    }
//}
//
//fun vulkanCleanup() {
//    if (vkDebugHandle != VK_NULL_HANDLE) vkDestroyDebugUtilsMessengerEXT(instance, vkDebugHandle, null)
//    if (debugUtils != null) {
//        debugUtils!!.pfnUserCallback().free()
//        debugUtils!!.free()
//    }
//    vkDestroyInstance(instance, null)
//}