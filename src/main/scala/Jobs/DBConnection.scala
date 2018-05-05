package Jobs

/**
  * Store MySQL connection properties so that I don't have to do this all over the place
  */
trait DBConnection {
  // MySQL settings
  private val host: String = "ec2-52-36-220-17.us-west-2.compute.amazonaws.com"
  private val port: Int = 26257
  private val database: String = "insight"
  private val user: String = "maxroach"
  private val password: String = ""
  
  // Build the inherited members
  final val connectionString = s"jdbc:mysql://$host:$port/$database?autoReconnect=true&useSSL=false"
  final val jdbcProperties = new java.util.Properties
  jdbcProperties.setProperty("driver", "org.postgresql.Driver")
  jdbcProperties.setProperty("user", s"$user")
  jdbcProperties.setProperty("password", s"$password")
}
