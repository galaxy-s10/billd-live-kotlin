package com.example.billd_live_kotlin

import android.content.Context
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription


class MyWebrtc {
    private val eglBaseContext = EglBase.create().eglBaseContext
    private val peerConnectionFactory: PeerConnectionFactory
    private val ctx: Context
    private lateinit var sdpObserver: SdpObserver
    var peerConnection: PeerConnection? = null

    constructor(applicationContext: Context) {
        ctx = applicationContext
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(ctx).createInitializationOptions()
        )
        peerConnectionFactory = createPeerConnectionFactory()
        start()
    }

    fun createPeerConnectionFactory(): PeerConnectionFactory {

        //先做默认配置，后面可能会遇到坑
        val options = PeerConnectionFactory.Options()
        val encoderFactory = DefaultVideoEncoderFactory(eglBaseContext, true, true)
        val decoderFactory = DefaultVideoDecoderFactory(eglBaseContext)
        return PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    fun createVideoCapture(context: Context): CameraVideoCapturer? {
        val enumerator: CameraEnumerator = if (Camera2Enumerator.isSupported(context)) {
            Camera2Enumerator(context)
        } else {
            Camera1Enumerator()
        }
        for (name in enumerator.deviceNames) {
            if (enumerator.isFrontFacing(name)) {
                return enumerator.createCapturer(name, null)
            }
        }
        for (name in enumerator.deviceNames) {
            if (enumerator.isBackFacing(name)) {
                return enumerator.createCapturer(name, null)
            }
        }
        return null
    }

    fun createAudioConstraints(): MediaConstraints {
        val audioConstraints = MediaConstraints()
        //回声消除
//        audioConstraints.mandatory.add(
//            MediaConstraints.KeyValuePair(
//                "googEchoCancellation",
//                "true"
//            )
//        )
//        //自动增益
//        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
//        //高音过滤
//        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
//        //噪音处理
//        audioConstraints.mandatory.add(
//            MediaConstraints.KeyValuePair(
//                "googNoiseSuppression",
//                "true"
//            )
//        )
        return audioConstraints
    }

    fun start() {
        val createAudioSource = peerConnectionFactory.createAudioSource(createAudioConstraints())
        val audioTrack =
            peerConnectionFactory.createAudioTrack("local_audio_track", createAudioSource)
        val rtcConfig = PeerConnection.RTCConfiguration(emptyList())
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        val localStream = peerConnectionFactory.createLocalMediaStream("stream")
        localStream.addTrack(audioTrack)
        // 创建 PeerConnection
        peerConnection = peerConnectionFactory.createPeerConnection(
            rtcConfig,
            object : CustomPeerConnectionObserver() {
                // 自定义的 PeerConnection 观察者，用于处理事件和回调
            })
        localStream.audioTracks.forEach { audioTrack ->
            peerConnection?.addTrack(audioTrack)
        }
        println("插入流")
        sdpObserver = CustomSdpObserver()
        println("sdpppppp")
        println(sdpObserver)
        val offer = peerConnection?.createOffer(sdpObserver, MediaConstraints())
        println(offer)
    }

    private inner class CustomSdpObserver : SdpObserver {
        override fun onCreateSuccess(sessionDescription: SessionDescription) {
            // 设置本地 SDP
            peerConnection?.setLocalDescription(object : SdpObserver {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {}
                override fun onSetSuccess() {
                    // 发送本地 SDP 给远程端
                    // 在此处你可以使用你选择的信令服务器或其他通信方式发送 SDP
                    println(" 发送本地 SDP 给远程端")
                }

                override fun onCreateFailure(error: String) {
                    println(" 发送本地 SDP 给远程端onCreateFailure")
                }

                override fun onSetFailure(error: String) {
                    println(" 发送本地 SDP 给远程端onSetFailure")
                }
            }, sessionDescription)
        }

        override fun onSetSuccess() {
            println("onSetSuccess---")
        }

        override fun onCreateFailure(error: String) {
            println("onCreateFailure---")
        }

        override fun onSetFailure(error: String) {
            println("onSetFailure---")
        }
    }


}


open class CustomPeerConnectionObserver : PeerConnection.Observer {
    override fun onIceCandidate(iceCandidate: IceCandidate) {
        // 处理 ICE 候选项
        println("处理 onIceCandidate")
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<out IceCandidate>) {
        // 处理移除的 ICE 候选项
        println("处理 onIceCandidatesRemoved")
    }

    override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {
        // 处理信令状态变化
        println("处理 onSignalingChange")
    }

    override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
        // 处理 ICE 连接状态变化
        println("处理 onIceConnectionChange")
    }

    override fun onIceConnectionReceivingChange(b: Boolean) {
        // 处理 ICE 连接接收状态变化
        println("处理 onIceConnectionReceivingChange")
    }

    override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {
        // 处理 ICE 收集状态变化
        println("处理 onIceGatheringChange")
    }

    override fun onAddStream(mediaStream: MediaStream) {
        // 处理添加的媒体流
        println("处理 onAddStream")
    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        // 处理移除的媒体流
        println("处理 onRemoveStream")
    }

    override fun onDataChannel(dataChannel: DataChannel?) {
        println("处理 onDataChannel")
    }


    override fun onRenegotiationNeeded() {
        // 处理重新协商需求
        println("处理 onRenegotiationNeeded")
    }

    override fun onAddTrack(rtpReceiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {
        // 处理添加的轨道
        println("处理 onAddTrack")
    }
}



