package pl.andrzejressel.monorepo.libs.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait Logging {
  private val _logger: LazyConstant[Logger] =
    LazyConstant.of(() => LoggerFactory.getLogger(getClass))

  protected[logging] def protectedLogger: Logger = _logger.get()

  protected object logger {
    def info(message: => String): Unit = _logger.get().info(message)
    def warn(message: => String): Unit = _logger.get().warn(message)
    def warn(message: => String, throwable: Throwable): Unit =
      _logger.get().warn(message, throwable)
    def error(message: => String): Unit = _logger.get().error(message)
    def error(message: => String, throwable: Throwable): Unit =
      _logger.get().error(message, throwable)
  }

}
