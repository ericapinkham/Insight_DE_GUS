package Jobs

/**
  * Store MySQL connection properties so that I don't have to do this all over the place
  */
trait MySQLConnection {
  // MySQL settings
  private val host: String = "ec2-35-161-183-67.us-west-2.compute.amazonaws.com"
  private val port: Int = 3306
  private val database: String = "insight"
  private val user: String = "root"
  private val password: String = "password"
  
  // Build the inherited members
  final val connectionString = s"jdbc:mysql://$host:$port/$database"
  final val jdbcProperties = new java.util.Properties
  jdbcProperties.setProperty("driver", "com.mysql.jdbc.Driver")
  jdbcProperties.setProperty("user", s"$user")
  jdbcProperties.setProperty("password", s"$password")
}
