package cn.hylexus.jt808.service.handler;

import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;


import java.util.Date;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.common.PackageData;
import cn.hylexus.jt808.common.Session;
import cn.hylexus.jt808.common.TPMSConsts;
import cn.hylexus.jt808.database.DBTools;
import cn.hylexus.jt808.database.dao.GPS_ALARM_STATUS_DAO;
import cn.hylexus.jt808.database.dao.GPS_LOCATION_REPORT_DAO;
import cn.hylexus.jt808.database.dao.GpsRegisterInfoDao;
import cn.hylexus.jt808.database.pojo.GPS_ALARM_STATUS;
import cn.hylexus.jt808.database.pojo.GPS_LOCATION_REPORT;
import cn.hylexus.jt808.database.pojo.GpsRegisterInfo;
import cn.hylexus.jt808.common.PackageData.MsgHeader;
import cn.hylexus.jt808.server.SessionManager;
import cn.hylexus.jt808.service.BaiduUploadService;
import cn.hylexus.jt808.service.TerminalMsgProcessService;
import cn.hylexus.jt808.service.codec.MsgDecoder;
import cn.hylexus.jt808.util.MD5Utils;
import cn.hylexus.jt808.vo.req.TerminalAuthenticationMsg;
import cn.hylexus.jt808.vo.req.TerminalRegisterMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class TCPServerHandler extends ChannelInboundHandlerAdapter { // (1)

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final SessionManager sessionManager;
	private final MsgDecoder decoder;
	private TerminalMsgProcessService msgProcessService;

	public TCPServerHandler() {
		this.sessionManager = SessionManager.getInstance();
		this.decoder = new MsgDecoder();
		this.msgProcessService = new TerminalMsgProcessService();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { // (2)
		try {
			ByteBuf buf = (ByteBuf) msg;
			if (buf.readableBytes() <= 0) {
				// ReferenceCountUtil.safeRelease(msg);
				return;
			}

			byte[] bs = new byte[buf.readableBytes()];
			buf.readBytes(bs);
			//转义还原
			//JT808ProtocolUtils utils = new JT808ProtocolUtils();
			//bs = utils.doEscape4Receive(bs, 0, bs.length);
			// 字节数据转换为针对于808消息结构的实体类
			PackageData pkg = this.decoder.bytes2PackageData(bs);
			// 引用channel,以便回送数据给硬件
			pkg.setChannel(ctx.channel());
			this.processPackageData(pkg);
		}catch(Exception e){
			//异常不做处理
		} 
		finally {
			release(msg);
		}
	}

	/**
	 * 
	 * 处理业务逻辑
	 * 
	 * @param packageData
	 * 
	 */
	private void processPackageData(PackageData packageData) {
		final MsgHeader header = packageData.getMsgHeader();

		// 1. 终端心跳-消息体为空 ==> 平台通用应答
		if (TPMSConsts.msg_id_terminal_heart_beat == header.getMsgId()) {
			logger.info(">>>>>[终端心跳],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				this.msgProcessService.processTerminalHeartBeatMsg(packageData);
				logger.info("<<<<<[终端心跳],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端心跳]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
			}
		}

		// 5. 终端鉴权 ==> 平台通用应答
		else if (TPMSConsts.msg_id_terminal_authentication == header.getMsgId()) {
			logger.info(">>>>>[终端鉴权],megid={},phone={},flowid={}", header.getMsgId(),header.getTerminalPhone(), header.getFlowId());
			try {
				TerminalAuthenticationMsg authenticationMsg = new TerminalAuthenticationMsg(packageData);
				this.msgProcessService.processAuthMsg(authenticationMsg);
				logger.info("<<<<<[终端鉴权],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端鉴权]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
			}
		}
		// 6. 终端注册 ==> 终端注册应答
		else if (TPMSConsts.msg_id_terminal_register == header.getMsgId()) {
			logger.info(">>>>>[终端注册],megid={},phone={},flowid={}", header.getMsgId(),header.getTerminalPhone(), header.getFlowId());
			SqlSession sqlSession = DBTools.getSession();

			try {
				TerminalRegisterMsg msg = this.decoder.toTerminalRegisterMsg(packageData);
				//生成鉴权码
				String phone = header.getTerminalPhone();
				String auth_token = MD5Utils.MD5Encode(phone, MD5Utils.salt);
				msg.setAuth_token(auth_token);
				this.msgProcessService.processRegisterMsg(msg);
				// 保存终端注册信息
				GpsRegisterInfoDao registerInfoDao = sqlSession.getMapper(GpsRegisterInfoDao.class);
				GpsRegisterInfo registerInfo = new GpsRegisterInfo();
				registerInfo.setPhone(header.getTerminalPhone());
				registerInfo.setImsi(msg.getTerminalRegInfo().getTerminalId());
				registerInfo.setImsi_type(msg.getTerminalRegInfo().getTerminalType());
				registerInfo.setLicense(msg.getTerminalRegInfo().getLicensePlate());
				registerInfo.setMake_brand(msg.getTerminalRegInfo().getManufacturerId());
				registerInfo.setProvince_id(msg.getTerminalRegInfo().getProvinceId());
				registerInfo.setCity_id(msg.getTerminalRegInfo().getCityId());
				registerInfo.setRegister_status(1);
				registerInfo.setOuter_color(msg.getTerminalRegInfo().getLicensePlateColor());
				registerInfo.setCreate_time(new Date());
				registerInfo.setAuth_token(auth_token);
				registerInfoDao.save(registerInfo);
				sqlSession.commit();
				logger.info("<<<<<[终端注册],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端注册]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
				sqlSession.rollback();
			}
		}
		// 7. 终端注销(终端注销数据消息体为空) ==> 平台通用应答
		else if (TPMSConsts.msg_id_terminal_log_out == header.getMsgId()) {
			logger.info(">>>>>[终端注销],megid={},phone={},flowid={}", header.getMsgId(),header.getTerminalPhone(), header.getFlowId());
			SqlSession sqlSession = DBTools.getSession();

			try {
				this.msgProcessService.processTerminalLogoutMsg(packageData);
				// 删除终端注册信息
				GpsRegisterInfoDao registerInfoDao = sqlSession.getMapper(GpsRegisterInfoDao.class);
				registerInfoDao.deleteByphone(header.getTerminalPhone());
				logger.info("<<<<<[终端注销],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端注销]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
				sqlSession.rollback();
			}
		}
		// 3. 位置信息汇报 ==> 平台通用应答
		else if (TPMSConsts.msg_id_terminal_location_info_upload == header.getMsgId()) {
			logger.info(">>>>>[位置信息],megid={},phone={},flowid={}",header.getMsgId(), header.getTerminalPhone(), header.getFlowId());

			SqlSession session = DBTools.getSession();
			
			try {
				LocationInfoUploadMsg locationInfoUploadMsg = this.decoder.toLocationInfoUploadMsg(packageData);
				//System.out.println(locationInfoUploadMsg);
				this.msgProcessService.processLocationInfoUploadMsg(locationInfoUploadMsg);
				//将终端位置信息上传到百度数据库中
				BaiduUploadService.addpoint(header, locationInfoUploadMsg);
				//将gps位置数据入库
				GPS_LOCATION_REPORT_DAO location_dao =  session.getMapper(GPS_LOCATION_REPORT_DAO.class);
				GPS_LOCATION_REPORT location = new GPS_LOCATION_REPORT();
				location.setID(MD5Utils.MD5Encode(locationInfoUploadMsg.toString(), MD5Utils.salt));
				location.setAlarm_field(locationInfoUploadMsg.getWarningFlagField());
				location.setStatus_field(locationInfoUploadMsg.getStatusField());
				location.setLatitude(locationInfoUploadMsg.getLatitude());
				location.setLongitude(locationInfoUploadMsg.getLongitude());
				location.setSpeed(locationInfoUploadMsg.getSpeed());
				location.setDirection(locationInfoUploadMsg.getDirection());
				location.setPhone(header.getTerminalPhone());
				location.setLoc_time(locationInfoUploadMsg.getTime());
				location.setCreate_time(new Date());
				location_dao.save(location);
				//报警信息入库
				int alert_field = locationInfoUploadMsg.getWarningFlagField();
				int flag = (alert_field & 0x40)>>>6;
				if (flag == 1) {
					//1：终端主电源⽋压
					GPS_ALARM_STATUS_DAO alarm_dao = session.getMapper(GPS_ALARM_STATUS_DAO.class);
					GPS_ALARM_STATUS alarm_info = new GPS_ALARM_STATUS();
					alarm_info.setAlarm_code("欠压报警");
					alarm_info.setAlarm_time(locationInfoUploadMsg.getTime());
					alarm_info.setPhone(header.getTerminalPhone());
					//alarm_info.setImsi(imsi);
					alarm_info.setLatitude(locationInfoUploadMsg.getLatitude());
					alarm_info.setLongitude(locationInfoUploadMsg.getLongitude());
					alarm_info.setPoi(BaiduUploadService.geocoder_location(locationInfoUploadMsg.getLongitude(), locationInfoUploadMsg.getLatitude()));
					alarm_dao.save(alarm_info);
				}
				session.commit();
				logger.info("<<<<<[位置信息],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[位置信息]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
				session.rollback();
			}
		}
		// 其他情况
		else {
			//logger.error(">>>>>>[未知消息类型],phone={},msgId={},package={}", header.getTerminalPhone(), header.getMsgId(),packageData);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		logger.error("发生异常:{}", cause.getMessage());
		cause.printStackTrace();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Session session = Session.buildSession(ctx.channel());
		sessionManager.put(session.getId(), session);
		logger.debug("终端连接:{}", session);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		final String sessionId = ctx.channel().id().asLongText();
		Session session = sessionManager.findBySessionId(sessionId);
		this.sessionManager.removeBySessionId(sessionId);
		logger.debug("终端断开连接:{}", session);
		ctx.channel().close();
		// ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
				logger.error("服务器主动断开连接:{}", session);
				ctx.close();
			}
		}
	}

	private void release(Object msg) {
		try {
			ReferenceCountUtil.release(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}