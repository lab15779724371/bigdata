package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tfit.BdBiProcSrvShEduOmc.dao.WarnLevelBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdMenuDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSuperviseUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdInterfaceColumnsDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdBriKitStoveDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdComplaintDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperContDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperSubjectDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdFoodSafetyGradeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMsgNoticeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionBodyDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionCandAnsDo;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdAddressLableObj;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdUserLableRelationObj;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
public interface Db2Service {	
    //从数据源ds2的数据表t_edu_supervise_user中查找用户名和密码（sha1字符串）以用户名（账号）
    TEduSuperviseUserDo getUserNamePassByUserName(String userName);
    
    //更新生成的token到数据源ds2的数据表t_edu_supervise_user表中
    boolean updateUserTokenToTEduSuperviseUser(String userName, String password, String token);
    
    //从数据源ds2的数据表t_edu_supervise_user中查找授权码以当前授权码
    String getAuthCodeByCurAuthCode(String token);
    
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息
    TEduBdUserDo getBdUserInfoByUserName(String userName);
    
    //从数据源ds2的数据表t_edu_supervise_user中查找授权码以当前授权码
    TEduBdUserDo getBdUserInfoByCurAuthCode(String token);
    
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息以单位ID
    List<TEduBdUserDo> getBdUserInfoByUserOrg(String orgId,Integer userType);
    
    //从数据源ds2的数据表t_edu_bd_user中查找授权码以当前授权码
    String getAuthCodeByCurAuthCode2(String token);
    
    //从数据源ds2的数据表t_edu_bd_user中查找所有用户信息
    List<TEduBdUserDo> getAllBdUserInfo();
    
    //从数据源ds2的数据表t_edu_bd_user中查找所有用户信息以父账户
    List<TEduBdUserDo> getAllBdUserInfoByParentId(String id,String parentId,Integer userType);
    
    //插入记录到数据源ds2的数据表t_edu_bd_user中
    boolean InsertBdUserInfo(TEduBdUserDo tebuDo);
    
    //更新记录到数据源ds2的数据表t_edu_bd_user中
    boolean UpdateBdUserInfo(TEduBdUserDo tebuDo, String token,boolean isClearnToken);
    
    //更新记录到数据源ds2的数据表t_edu_bd_user中以输入字段
    boolean UpdateBdUserInfoByField(TEduBdUserDo tebuDo, String fieldName, String fieldVal);
    
    //删除数据源ds2的数据表t_edu_bd_user中记录以用户名
    boolean DeleteBdUserInfoByUserName(String userName);
    
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息以授权码token
    TEduBdUserDo getBdUserInfoByToken(String token);
    
    //插入记录到数据源ds2的数据表t_edu_bd_role中
    boolean InsertBdRoleInfo(TEduBdRoleDo tebrDo);
    
    //更新记录到数据源ds2的数据表t_edu_bd_role中以输入字段
    boolean UpdateBdRoleInfoByField(TEduBdRoleDo tebrDo, String fieldName, String fieldVal);
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以id
    TEduBdRoleDo getBdRoleInfoByRoleId(String id);
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    TEduBdRoleDo getBdRoleInfoByRoleName(String roleName);
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    List<TEduBdRoleDo> getBdRoleInfoByRoleName2(String roleName);
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    TEduBdRoleDo getBdRoleInfoByRoleName3(int roleType, String roleName);
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    List<TEduBdRoleDo> getBdRoleInfoByRoleName4(int roleType, String roleName);
    
    //从数据源ds2的数据表t_edu_bd_role中查找所有角色名称
    List<TEduBdRoleDo> getBdRoleInfoAllRoleNames();
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色名称以角色类型
    List<TEduBdRoleDo> getBdRoleInfoRoleNamesByRoleType(int roleType);
    
    //从数据源ds2的数据表t_edu_bd_role中查找所有角色信息
    List<TEduBdRoleDo> getAllBdRoleInfo();
    
    //删除数据源ds2的数据表t_edu_bd_role中记录以角色名
    boolean DeleteBdRoleInfoByRoleName(String roleName);
    
    //插入记录到数据源ds2的数据表t_edu_bd_user_perm中
    boolean InsertBdUserPermInfo(TEduBdUserPermDo tebrpDo);
    
    //插入记录到数据源ds2的数据表t_edu_bd_user_perm中
    boolean InsertBdUserPermInfo(List<TEduBdUserPermDo> tebrpDoList);
    
    //更新记录到数据源ds2的数据表t_edu_bd_user_perm中以输入字段
    boolean UpdateBdRolePermInfoByField(TEduBdUserPermDo tebrpDo, String fieldName, String fieldVal);
    
    //删除数据源ds2的数据表t_edu_bd_user_perm中记录以用户名
    boolean DeleteBdUserPermInfoByUserId(String userId);
    
    //从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
    List<TEduBdUserPermDo> getAllBdUserPermInfo(String userId, int permType);
    
    //从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
    List<TEduBdMenuDo> getBdMenuInfoByLevel(int level);
    
    //从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别和父菜单ID
    List<TEduBdMenuDo> getBdMenuInfoByLevel(int level, String parentId);
    
    //插入消息通知记录
    int insertMsgNotice(TEduBdMsgNoticeDo tebmnDo);
    
    //获取所有用户名、单位ID、单位名称记录信息
    List<EduBdUserDo> getAllUserInfos(String id,Integer userType);
    
    //插入消息通知状态记录
    int insertMsgNoticeStatus(TEduBdNoticeStatusDo tebnsDo);
    
    //查询消息通知状态记录列表以接收用户名
    List<TEduBdNoticeStatusDo> getMsgNoticeStatusByRcvUserName(String rcvUserName);
    
    //查询消息通知记录以通知id
    TEduBdMsgNoticeDo getMsgNoticeById(String id);
    
    //查询消息通知状态记录列表以接收用户名
    List<TEduBdNoticeStatusDo> getMsgNoticeStatusBySendUserName(String sendUserName);
    
    //查询消息通知状态记录列表以通知ID和发布用户名
    List<TEduBdNoticeStatusDo> getMsgNoticeStatusBybIdSendUser(String bulletinId, String sendUserName);
    
    //查询消息通知状态记录列表以通知id和接收用户名
    TEduBdNoticeStatusDo getMsgNoticeStatusBybIdRcvUserName(String bulletinId, String rcvUserName);
  	
  	//更新阅读次数
    int updateReadCountInMsgNotice(String bulletinId, String rcvUserName, int readCount);
    
    //更新签到标识
    int updateSignFlagByTEduBdNoticeStatusDo(TEduBdNoticeStatusDo tebnsDo);
    
    //查询消息通知当前上一条记录以当前通知id
    TEduBdMsgNoticeDo getPreMsgNoticeById(String id);
    
    //查询消息通知当前下一条记录以当前通知id
    TEduBdMsgNoticeDo getNextMsgNoticeById(String id);
    
    //查询所有子用户记录信息以父用户id
    List<EduBdUserDo> getAllSubUserInfosByParentId(String orgId,String parentId,Integer userType);
    
    //查询用户记录信息以用户id
    EduBdUserDo getUserInfoByUserId(String id);
    
    //查询消息通知当前上一条记录以当前通知id和接收用户名（接收用户名字串前后添加%）
  	TEduBdMsgNoticeDo getPreMsgNoticeByIdRcvUserName(String id, String rcvUserName);
  	
  	//查询消息通知当前下一条记录以当前通知id和接收用户名（接收用户名字串前后添加%）
  	TEduBdMsgNoticeDo getNextMsgNoticeByIdRcvUserName(String id, String rcvUserName);  	
  	
  	//查询消息通知当前上一条记录以当前通知id和接收用户名（发送用户名）
  	TEduBdMsgNoticeDo getPreMsgNoticeByIdSendUserName(String id, String sendUserName);
  			
  	//查询消息通知当前下一条记录以当前通知id和接收用户名（发送用户名）
  	TEduBdMsgNoticeDo getNextMsgNoticeByIdSendUserName(String id, String sendUserName);
  	
  	//查询邮件服务记录以用户名
  	TEduBdMailSrvDo getMailSrvInfoByUserName(String userName);
  	
  	//插入邮件服务记录
  	int insertMailSrv(TEduBdMailSrvDo tebmsDo);
  	
  	//更新邮件服务记录
  	boolean updateMailSrv(TEduBdMailSrvDo tebmsDo);
  	
  	//查询学校视频监控记录信息以学校id
    List<TEduBdBriKitStoveDo> getSchVidSurvInfosBySchId(String schoolId);
    
    //查询所有学校视频监控记录信息
    List<TEduBdBriKitStoveDo> getAllSchVidSurvInfos();
    
    List<TEduBdBriKitStoveDo> getSchVidSurvInfosByDistId(String regionId);
    
    /**
     * 插入用户设置动态列
     * @param record
     * @return
     */
    public int addUserInterfaceColums(EduBdInterfaceColumnsDo record) ;

    /**
     * 根据主键修改用户设置的动态列
     * @param record
     * @return
     */
    public int updateUserInterfaceColumsByPrimaryKey(EduBdInterfaceColumnsDo record);
    
    
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    public EduBdInterfaceColumnsDo getByInterfaceName(String userId,String interfaceName);
    
    /**
     * 获取t_edu_bd_interface_columns最大的编号
     * @return
     */
    public Integer getInterfaceColumnsMaxId();
    
    //插入教育视频记录
    int insertTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
    
    //更新教育视频记录
    int updateTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
    
    //获取教育视频以记录ID
    TEduBdEtvidLibDo getTEduBdEtvidLibDoById(String id);
    
    //获取所有教育视频
    List<TEduBdEtvidLibDo> getAllTEduBdEtvidLibDos();
    
    //获取教育视频以开始和结束时间
    List<TEduBdEtvidLibDo> getTEduBdEtvidLibDosByCreateTime(String startTime, String endTime);
    
    //删除教育视频记录以记录ID
    int deleteTEduBdEtvidLibDoById(String id);
    
    //获取所有食品安全等级记录
    List<TEduBdFoodSafetyGradeDo> getAllTEduBdFoodSafetyGradeDos();
    	
    //获取食品安全等级记录以区域名称
    List<TEduBdFoodSafetyGradeDo> getTEduBdFoodSafetyGradeDoByDistName(String distName);
    
    //插入投诉举报记录
    int insertTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
    	
    //获取投诉举报以记录ID
    TEduBdComplaintDo getTEduBdComplaintDoById(String id);
    
    //更新投诉举报
    int updateTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
    
    //获取投诉举报以日期段，日期格式：xxxx-xx-xx
    List<TEduBdComplaintDo> getTEduBdComplaintDosBySubDate(String startDate, String endDate);
    
    //获取所有投诉举报
    List<TEduBdComplaintDo> getAllTEduBdComplaintDos();
    
    //获取所有试卷
    List<TEduBdExamPaperDo> getAllTEduBdExamPaperDo();
    
    //获取所有试卷信息以试卷ID
    TEduBdExamPaperDo getTEduBdExamPaperDoById(String id);
    
    //获取所有试卷内容以试卷ID
    List<TEduBdExamPaperContDo> getTEduBdExamPaperContDosByEpId(String epId);
    
    //获取试题以试题ID
    TEduBdQuestionBodyDo getTEduBdQuestionBodyDoById(String id);
    
    //获取候选答案以试题ID
    List<TEduBdQuestionCandAnsDo> getTEduBdQuestionCandAnsDoByQuestionId(String questionId);
    
    //获取所有试卷大题型主题以试卷ID和试题类型
    TEduBdExamPaperSubjectDo getTEduBdExamPaperSubjectDoByEpIdQuestionType(String epId, int questionType);
    /**任务中心**/
	// 1.0  根据任务id查询任务信息
	public AppCommonDao getCheckTask(String id);
    
    //1.1 添加任务
    boolean getAddTask(LinkedHashMap<String, Object> filterParamMap);
    
    //1.2 任务列表
    List<AppCommonDao> getTaskList(LinkedHashMap<String, Object> filterParamMap);
    
    //1.3 我的发布
    List<AppCommonDao> getMyReleaseList(LinkedHashMap<String, Object> filterParamMap);
    
    //1.3.1 修改任务
    boolean getUpdateTask(LinkedHashMap<String, Object> filterParamMap,String id);
    
    //1.3.2 删除任务
    boolean getDeleteTask(LinkedHashMap<String, Object> filterParamMap);
    
    //1.4 市/区教育局下拉列表
    List<AppCommonDao> geteduList();
    //1.4.1 根据市区,获取承办人列表
    List<AppCommonDao> getoperatorList(String org_name);
    
  	/** 事务申办 **/
    //2.0 申办详情
    AppCommonDao getAffairDetail(String id);
    
  	//2.1 事务申办
    boolean getAddAffair(LinkedHashMap<String, Object> filterParamMap);
    //2.2 申办列表
    List<AppCommonDao> getAffairList(LinkedHashMap<String, Object> filterParamMap);
    //2.4事务办结
    boolean getCompleteAffair(LinkedHashMap<String, Object> filterParamMap,String id);
    
    //2.5 我的申办
    List<AppCommonDao> getMyApplyListList(LinkedHashMap<String, Object> filterParamMap);
    //2.6 修改我的申办
    boolean getUpdateMyaffair(LinkedHashMap<String, Object> filterParamMap,String id);
    //2.7 删除我的申办
    boolean getDeleteMyaffair(String id);
    
    /** 预警信息 **/    
    //3.1 下周未排菜预警    
    AppCommonDao getLwUnscheduledAlert(String user_account,String category,String pushRecipient);    
    boolean getLwUnscheduledAlertUpdate(LinkedHashMap<String, Object> filterParamMap,String user_account,String category);
    
	/** 发送信息**/
	//4.0 消息展示接口
    List<AppCommonDao> getSysInfoDisplay(LinkedHashMap<String, Object> filterParamMap);
    //4.0.1 未读信息数
    AppCommonDao getNoReadInfoNum(LinkedHashMap<String, Object> filterParamMap);
    //4.0.2 推送信息
    boolean getInsertInfo(LinkedHashMap<String, Object> filterParamMap);
    
    //更新数据
    boolean getUpdateSysInfo(String task_category,String user_account,String task_time);
    
    //定时任务
    List<AppCommonDao> getScheduleTask();
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息以user_account
    TEduBdUserDo getBdUserInfoByUserAccount(String user_account);
    //保存下周排菜数据
    boolean getInsertLwUnscheduledAlert(List<LinkedHashMap<String, Object>> dataList);
    //获取下周排菜数据
    List<AppCommonDao> getLwUnscheduledAlert(String taskTime,String pushUserAccount);
    
    //保存今天排菜数据
    boolean getInsertTodayUnscheduledAlert(List<LinkedHashMap<String, Object>> dataList);
    //获取下周排菜数据
    List<AppCommonDao> getTodayUnscheduledAlert(String taskTime,String pushUserAccount);
    
    //保存今天排菜数据
    boolean getInsertUnacceptedWarning(List<LinkedHashMap<String, Object>> dataList);
    //获取下周排菜数据
    List<AppCommonDao> getUnacceptedWarning(String taskTime,String pushUserAccount);
    
    //保存今天排菜数据
    boolean getInsertNotSampleWarning(List<LinkedHashMap<String, Object>> dataList);
    //获取下周排菜数据
    List<AppCommonDao> getNotSampleWarning(String taskTime,String pushUserAccount);
    
    //保存今天排菜数据
    boolean getInsertSteakDataAnomalyWarning(List<LinkedHashMap<String, Object>> dataList);
    //获取下周排菜数据
    List<AppCommonDao> getSteakDataAnomalyWarning(String taskTime,String pushUserAccount);
    
    //保存今天排菜数据
    boolean getInsertAcceptanceDataAnomalyWarning(List<LinkedHashMap<String, Object>> dataList);
    //获取下周排菜数据
    List<AppCommonDao> getAcceptanceDataAnomalyWarning(String taskTime,String pushUserAccount);

    /** 
     * @Description: 获取预警规则
     * @Param: [warnType, warnAlertType] 
     * @return: java.util.List<com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao> 
     * @Author: jianghy 
     * @Date: 2020/1/14
     * @Time: 14:19       
     */
    List<AppCommonDao> getCheckWarnSetting(Integer warnType,Integer warnAlertType);

    /** 
     * @Description: 插入预警规则 
     * @Param: [wlb] 
     * @return: void 
     * @Author: jianghy 
     * @Date: 2020/1/15
     * @Time: 16:00       
     */
    void insertWarnRuleSetting(WarnLevelBody wlb);

    /** 
     * @Description: 修改预警规则
     * @Param: [wlb] 
     * @return: void 
     * @Author: jianghy 
     * @Date: 2020/1/15
     * @Time: 16:00       
     */
    void updateWarnRuleSetting(WarnLevelBody wlb);

    
    /** 
     * @Description: 插入预警标题 
     * @Param: [map] 
     * @return: void 
     * @Author: jianghy 
     * @Date: 2020/1/17
     * @Time: 17:40       
     */
    void insertWarnTitle(Map<String,Object> map);


    /** 
     * @Description: 插入预警内容
     * @Param: [list] 
     * @return: void 
     * @Author: jianghy 
     * @Date: 2020/1/17
     * @Time: 17:41       
     */
    void insertWarnContent(List<LinkedHashMap<String,Object>> list);


    /**
     * @Description: 根据预警标题id修改读取状态
     * @Param: [titleId]
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/17
     * @Time: 17:45
     */
    void updateWarnReadSatus(String titleId);


    /**
     * @Description: 根据tileId
     * @Param: [titleId]
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/17
     * @Time: 17:53
     */
    List<AppCommonDao> getWarnListByTitleId(String titleId);


    /** 
     * @Description: 根据用户的读取状态获取未读和已读的列表
     * @Param: [readsStatus, userAccount] 
     * @return: java.util.List<com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao> 
     * @Author: jianghy 
     * @Date: 2020/1/18
     * @Time: 11:20
     */
    List<AppCommonDao> getWarnTitleByReadStatus(Integer readsStatus,String userAccount,String id);
    
    public List<AppCommonDao> getWarnTitleById(String id);

    //获取未读信息数
    AppCommonDao getNoReadWarnInfoNum(Integer readsStatus,String userAccount);
    
    
	//*******************通讯录相关*********************************
    public int deleteByPrimaryKeyAddressLable(Integer id);
    public int insertAddressLable(TEduBdAddressLableObj record);
    public TEduBdAddressLableObj selectByPrimaryKeyAddressLable(Integer id);
    public List<TEduBdAddressLableObj> selectListAddressLable(TEduBdAddressLableObj tEduBdAddressLableObj,Integer startNum,Integer pageSize);
    public int selectListAddressLableCount(TEduBdAddressLableObj tEduBdAddressLableObj);
    public List<TEduBdAddressLableObj> selectListAndUserCount(TEduBdAddressLableObj tEduBdAddressLableObj,Integer startNum,Integer pageSize);
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    public Integer getAddressLableMaxId();

    public int updateByPrimaryKeySelectiveAddressLable(TEduBdAddressLableObj record);
    public int deleteByPrimaryKeyUserLableRelation(String userId,Integer lableId) ;
    public int deleteByLableId(Integer lableId);
    public int insertUserLableRelation(TEduBdUserLableRelationObj record);
    //public TEduBdUserLableRelationObj selectByPrimaryKeyUserLableRelation(Integer id);
    public int updateByPrimaryKeySelectiveUserLableRelation(TEduBdUserLableRelationObj record);
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    public List<TEduBdUserLableRelationObj> selectListAddressLableRelation(TEduBdUserLableRelationObj tEduBdAddressLableObj,Integer startNum,Integer pageSize);
    
    public List<TEduBdUserDo> getBdUserInfoByUserLableRelation(Integer lableId) ;
}
