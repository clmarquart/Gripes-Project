import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.*

appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%level %logger - %msg%n"
  }
}

logger("org.hibernate", DEBUG, ["STDOUT"])
root(DEBUG, ["STDOUT"])