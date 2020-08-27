package doracore.util
import com.datastax.oss.driver.api.core.uuid.Uuids
object MyUUID {
  def getUUIDString()={
    Uuids.timeBased().toString
  }
}
