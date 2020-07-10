package com.jd.platform.hotkey.dashboard.util;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.LockResponse;
import com.jd.platform.hotkey.common.configcenter.etcd.JdEtcdClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class LockUtil{
   private static final Log logger = LogFactory.getLog(LockUtil.class);
   @Resource
   private JdEtcdClient client;


   public long lock(String lockName) {

      long leaseId = 0;
      try {
         leaseId = client.getLeaseClient().grant(30).sync().getID();
         LockResponse lr = client.getLockClient().lock(ByteString.copyFromUtf8(lockName)).withLease(leaseId).timeout(100).sync();
         return leaseId;
      } catch (Exception e) {
         client.getLeaseClient().revoke(leaseId);
      }
      return 0;
   }

   /**
    * 解锁操作，释放锁、解除租约
    * @param lockName:锁名
    */
   public void unLock(String lockName, long leaseId) {
      logger.debug("start to unlock required resource:[" + lockName + "]" + " whth lease[" + leaseId + "]");
      try {
         // 释放锁
         client.getLockClient().unlock(ByteString.copyFromUtf8(lockName)).sync();
         // 删除租约
         if (0 != leaseId) {
            client.getLeaseClient().revoke(leaseId);
         }
      } catch (Exception e) {
         logger.error("unlock resource [" + lockName + "]" + "failed." + e);
      }
   }

}
