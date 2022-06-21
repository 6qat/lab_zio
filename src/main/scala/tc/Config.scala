package tc

object Config {

  case class AppConfig(
      nodeConfig: NodeConfig
  )

  case class NodeConfig(host: String, port: Int)

}
