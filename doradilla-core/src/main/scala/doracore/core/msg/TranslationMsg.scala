package doracore.core.msg

/**
  * For doradilla.core.msg in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
object TranslationMsg {

  case class TranslatedTask(task: Any)

  trait TranslationError

  case class TranslationOperationError(operation: Option[String]) extends TranslationError

  case class TranslationDataError(data: Option[Any]) extends TranslationError

}
