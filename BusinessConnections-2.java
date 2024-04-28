import java.sql.*;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;

/**
 * This is a Business Connections App that will help you store and find your connections and their companies when needed.
 *
 */


public class BusinessConnections implements AutoCloseable {

	// Default connection information
	private static final String DB_NAME = "hs0420_Business_Connections";
	private static final String DB_USER = "token_a347";
	private static final String DB_PASSWORD = "1f4GMma2ynZ9BIGd";
	

	private static final String SEARCH_FOR_CONNECTION_BY_NAME = 
			"SELECT Connection.id, Connection.name, Connection.position, Connection.email, Connection.phoneNumber\n"
					+ "FROM Connection\n"
					+ "WHERE Connection.name like ?"
					+ "ORDER BY Connection.id";

	private static final String ADD_NEW_CONNECTION =
			"INSERT INTO Connection (name,position,email,phoneNumber) VALUES\n"
					+ "(?, ?, ?, ?)";

	private static final String SHOW_CONNECTIONS = 
			"SELECT Connection.id, Connection.name, Connection.position, Connection.email, Connection.phoneNumber\n"
					+ "FROM Connection\n"
					+ "ORDER BY Connection.id"
					;

	private static final String SHOW_COMPANIES = 
			"SELECT id, companyName\n"
					+ "FROM Company\n"
					+ "ORDER BY Company.id";

	private static final String SHOW_CONENCTIONS_BY_COMPANY_ID = 
			"SELECT Connection.id, Connection.name, Connection.position, Connection.email, Connection.phoneNumber\n"
					+ "FROM Connection\n"
					+ "INNER JOIN InCompany ON Connection.id = connection_id\n"
					+ "INNER JOIN Company ON Company.id = company_id\n"
					+ "where Company.id = ?"
					+ "ORDER BY Connection.id";


	/*private static final String GET_TOTAL_BUSINESS_CONNECTIONS =
			"SELECT COUNT(id)\n"
			+ "FROM Connection";*/

	/*private static final String GET_TOTAL_BUSINESS_CONNECTIONS_BY_COMPANY =
			"SELECT Company.companyName, COUNT(Connection.id) as count\n"
			+ "FROM Connection\n"
			+ "LEFT JOIN InCompany ON Connection.id = connection_id\n"
			+ "LEFT JOIN Company ON company_id = Company.id\n"
			+ "GROUP BY Company.id";*/

	private static final String GET_COMPANY_WITH_MOST_BUSINESS_CONNECTIONS =
			"SELECT Company.id, companyName, COUNT(Connection.id) as highestCount\n"
					+ "FROM Connection\n"
					+ "INNER JOIN InCompany ON Connection.id = connection_id\n"
					+ "INNER JOIN Company ON company_id = Company.id\n"
					+ "GROUP BY Company.id\n"
					+ "HAVING highestCount = (\n"
					+ "	SELECT MAX(ConnectionCount.count) as maxCount\n"
					+ "	FROM Company \n"
					+ "	INNER JOIN (\n"
					+ "		SELECT Company.id, COUNT(Connection.id) as count\n"
					+ "		FROM Connection\n"
					+ "		INNER JOIN InCompany ON Connection.id = connection_id\n"
					+ "		INNER JOIN Company ON company_id = Company.id\n"
					+ "		GROUP BY Company.id\n"
					+ "	) as ConnectionCount ON Company.id = ConnectionCount.id\n)"
					+ "ORDER BY Company.id";

	private static final String SHOW_NOTES_FOR_CONNECTION_BY_ID = 
			"SELECT dateTaken, text\n"
					+ "From Note\n"
					+ "INNER JOIN Connection ON Connection.id = Note.connection_id\n"
					+ "WHERE Connection.id = ?";

	private static final String SHOW_NOTES_FOR_COMPANY_BY_ID = 
			"SELECT dateTaken, text\n"
					+ "From Note\n"
					+ "INNER JOIN Company ON Company.id = Note.company_id\n"
					+ "WHERE Company.id = ?";

	private static final String ADD_NOTE = 
			"INSERT INTO Note (text, dateTaken, connection_id, company_id) VALUES\n"
					+ "(?, ?, ?, ?)";

	private static final String ADD_COMPANY = 
			"INSERT INTO Company (companyName) VALUES\n"
					+ "(?)";

	private static final String ADD_RELATIONSHIP = 
			"INSERT INTO InCompany (connection_id,company_id) VALUES\n"
					+ "(?,?)";

	private static final String DROP_CONNECTION = 
			"DELETE FROM Connection \n"
					+ "WHERE id = ?";

	private static final String DROP_COMPANY = 
			"DELETE FROM Company \n"
					+ "WHERE id = ?";







	private PreparedStatement serachForConnectionByName;
	private PreparedStatement addNewConnection;
	private PreparedStatement showConnections;
	private PreparedStatement showCompanies;
	private PreparedStatement showConnectionsByCompanyId;
	//	private PreparedStatement getTotalBusinessConnectionsByCompany;
	private PreparedStatement getCompanyWithMostBusinessConnections;
	private PreparedStatement showNotesForConnectionsById;
	private PreparedStatement showNotesForCompanyById;
	private PreparedStatement addCompany;
	private PreparedStatement addNote;
	private PreparedStatement addRelationship;
	private PreparedStatement dropConnection;
	private PreparedStatement dropCompany;


	// Connection information to use
	private final String dbHost;
	private final int dbPort;
	private final String dbName;
	private final String dbUser, dbPassword;

	// The database connection
	private Connection connection;


	/**
	 * @param sshKeyfile the filename of the private key to use for ssh
	 * @param dbName the name of the database to use
	 * @param dbUser the username to use when connecting
	 * @param dbPassword the password to use when connecting
	 * @throws SQLException if unable to connect
	 */
	public BusinessConnections(String dbHost, int dbPort, String dbName,
			String dbUser, String dbPassword) throws SQLException {
		this.dbHost = dbHost;
		this.dbPort = dbPort;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;

		connect();
	}

	private void connect() throws SQLException {
		// URL for connecting to the database: includes host, port, database name,
		// user, password
		final String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
				dbHost, dbPort, dbName,
				dbUser, dbPassword
				);

		// Attempt to connect, returning a Connection object if successful
		this.connection = DriverManager.getConnection(url);

		// Preparing the statements (queries) that we will be executed

		this.serachForConnectionByName = this.connection.prepareStatement(SEARCH_FOR_CONNECTION_BY_NAME);
		this.addNewConnection = this.connection.prepareStatement(ADD_NEW_CONNECTION );
		this.showConnections = this.connection.prepareStatement(SHOW_CONNECTIONS);
		this.showCompanies = this.connection.prepareStatement(SHOW_COMPANIES);
		this.showConnectionsByCompanyId = this.connection.prepareStatement(SHOW_CONENCTIONS_BY_COMPANY_ID);
		//this.getTotalBusinessConnectionsByCompany = this.connection.prepareStatement(GET_TOTAL_BUSINESS_CONNECTIONS_BY_COMPANY);
		this.getCompanyWithMostBusinessConnections = this.connection.prepareStatement(GET_COMPANY_WITH_MOST_BUSINESS_CONNECTIONS);
		this.showNotesForConnectionsById = this.connection.prepareStatement(SHOW_NOTES_FOR_CONNECTION_BY_ID);
		this.showNotesForCompanyById = this.connection.prepareStatement(SHOW_NOTES_FOR_COMPANY_BY_ID);
		this.addCompany = this.connection.prepareStatement(ADD_COMPANY);
		this.addNote = this.connection.prepareStatement(ADD_NOTE);
		this.addRelationship = this.connection.prepareStatement(ADD_RELATIONSHIP);
		this.dropConnection = this.connection.prepareStatement(DROP_CONNECTION);
		this.dropCompany = this.connection.prepareStatement(DROP_COMPANY);
	}

	/**
	 * Runs the application.
	 */
	public void runApp() throws SQLException {


		Scanner in = new Scanner(System.in);


		while (true) {

			System.out.println("\nWhat would you like to do?\n\n"
					+ "\tA: Search for a connection by name\n\n"
					+ "\tB: See all conections\n"
					+ "\tC: See all companies\n"
					+ "\tD: See Connections connected to a spesific company\n"
					+ "\tE: See the company you have most connections from\n"
					+ "\tF: See notes for a connection or a company\n\n"
					+ "\tG: Add a connection\n"
					+ "\tH: Add a company\n"
					+ "\tI: Add a note to a connetion or a company\n\n"
					+ "\tJ: Connect a company to a connection\n\n"
					+ "\tK: Drop a Connection\n"
					+ "\tL: Drop a Company\n\n"
					+ "\tQ: Quit");



			String line2 = in.nextLine();
			String line =line2.toUpperCase();


			if( line.equals("A")) {
				System.out.println("Enter a name to search for: ");
				String name = in.nextLine();
				serachForConnectionByName(name);


				System.out.println("\nTo see notes on the connection enter their id number or hit enter to see other options."); //the hit enter does not work
				String temp = in.nextLine();
				if (!temp.equals(null) ) {
					
					showNotesForConnectionsById(temp);
				}
				else {
					System.out.println("Invalid input");
				}

			}
			else if (line.equals("G")) {
				System.out.println("Enter the name of your new connection:");
				String name = in.nextLine();

				System.out.println("Enter the position of your new connection:");
				String position = in.nextLine();

				System.out.println("Enter the email of your new connection. If you don't have it hit enter. ");
				String email = in.nextLine();

				System.out.println("Enter the phone number of your new connection. If you don't have it hit enter. ");
				String phoneNumber = in.nextLine();

				addNewConnection(name, position, email, phoneNumber);
			}

			else if (line.equals("B")) {
				showConnections();

				System.out.println("\nTo see notes on a connection enter their id number or hit enter to see other options.");

				String temp = in.nextLine();
				if (!temp.equals(null)) {
					
					showNotesForConnectionsById(temp);

				}
				else {
					System.out.println("Invalid input");
				}
			}

			else if (line.equals("C")) {
				showCompanies();

				System.out.println("\nTo see notes on a Company enter its id number or hit enter to see other options.");

				String temp = in.nextLine();
				if (!temp.equals(null)) {
					
					showNotesForCompanyById(temp);
				}

				else {
					System.out.println("Invalid input");
				}
			}

			else if (line.equals("E")) {
				getCompanyWithMostBusinessConnections();
			}

			else if (line.equals("H")) {
				System.out.println("Enter the name of the company you want to add:");
				String name = in.nextLine();

				addCompany(name);
				//If duplicates are added the program crashes
			

			}

			else if (line.equals("I")) {
				System .out.println("Do you want to add a note to a company or a connection? (company/connection)");
				String temp = in.nextLine();
				if (temp.equals("company")) {
					showCompanies();
					System.out.println("Please enter company id.");
					String id = in.nextLine();
					System.out.println("Please enter your note.");
					String text = in.nextLine();
					System.out.println("Please enter todays date. (YYYY-MM-DD)");
					String date = in.nextLine();
					addNote(text, date, null, id);
				}
				else if (temp.equals("connection")) {
					showConnections();
					System.out.println("Please enter connection id.");
					String id = in.nextLine();
					System.out.println("Please enter your note.");
					String text = in.nextLine();
					System.out.println("Please enter todays date. (YYYY-MM-DD)");
					String date = in.nextLine();
					addNote(text, date, id, null);
				}
				else {
					System.out.println("Invalid input");
				}
			}
			else if (line.equals("F")) {
				System .out.println("Do you want to see the notes for a company or a connection? (company/connection)");
				String temp = in.nextLine();
				if (temp.equals("company")) {
					showCompanies();
					System.out.println("Please enter company id.");
					String id = in.nextLine();
					showNotesForCompanyById(id);
					
				}
				else if (temp.equals("connection")) {
					showConnections();
					System.out.println("Please enter connection id.");
					String id = in.nextLine();
					showNotesForConnectionsById(id);
				
				}
				else {
					System.out.println("Invalid input");
				}
			}
			// Crashes with an invalid input
			else if (line.equals("J")) {
				showConnections();
				System.out.println("Enter ID for the connection you want to connect to a company");
				String connection_id = in.nextLine();

				showCompanies();
				System.out.println("Enter the ID for the company");
				String company_id = in.nextLine();
				// Adds the relationship but will crash if someone enters an invalid input
				addRelationship(connection_id, company_id);
			}

			else if (line.equals("L")) {
				showCompanies();
				System.out.println("Enter ID for the company you want to DELETE");
				String id = in.nextLine();
				
				dropCompany(id);
			}

			else if (line.equals("K")) {
				showConnections();
				System.out.println("Enter ID for the connection you want to DELETE");
				String id = in.nextLine();
				
				dropConnection(id);
			}

			else if (line.equals("D")) {
				showCompanies();
				System.out.println("Enter ID for the company you want to see your connections at");
				String id = in.nextLine();
				
				showConnectionsByCompanyId(id);

			}

			else if (line.equals("Q")) {
				System.out.println("Okey, bye!");
				break;
			}

			else {
				System.out.println("Invalid input. Please try again.");
			}


		}
	}


	//Searching for Connections by name
	public void serachForConnectionByName(String name) throws SQLException {

		serachForConnectionByName.setString(1,"%" + name + "%");

		ResultSet results = serachForConnectionByName.executeQuery();

		if(results.next() == false) {
			System.out.println("You have no connection with this name.");
		}
		else {
			int id = results.getInt("id");
			String name1 = results.getString("name");
			String position = results.getString("position");
			String email = results.getString("email");
			String phoneNumber = results.getString("phoneNumber");
			
			System.out.println("Id: "+ id + ", Name: " + name1 + ", Position: " + position + ", Email: " + email + ", Number: " + phoneNumber );
		}
		// Iterate over each row of the results
		while (results.next()) {
			int id = results.getInt("id");
			String name1 = results.getString("name");
			String position = results.getString("position");
			String email = results.getString("email");
			String phoneNumber = results.getString("phoneNumber");
		
			System.out.println("Id: "+ id + ", Name: " + name1 + ", Position: " + position + ", Email: " + email + ", Number: " + phoneNumber );
		}
	}

	// adding new connections
	public void addNewConnection(String name, String position, String email, String phoneNumber) throws SQLException {

		addNewConnection.setString(1, name);
		addNewConnection.setString(2, position);
		addNewConnection.setString(3, email);
		addNewConnection.setString(4, phoneNumber);

		addNewConnection.execute();

		// This might not get the correct id if there are connections with the exact same name. 

		System.out.println("\nCongratulations! you have a new connection.\n");
		serachForConnectionByName(name);



	} 

	public void showConnections() throws SQLException {

		ResultSet results = showConnections.executeQuery();

		if(results.next() == false) {
			System.out.println("You have no connections yet. But you can add one by entering C");
		}
		else {
			int id = results.getInt("id");
			String name1 = results.getString("name");
			String position = results.getString("position");
			String email = results.getString("email");
			String phoneNumber = results.getString("phoneNumber");
			
			System.out.println("Id: "+ id + ", Name: " + name1 + ", Position: " + position + ", Email: " + email + ", Number: " + phoneNumber );
		}
		// Iterate over each row of the results
		while (results.next()) {
			int id = results.getInt("id");
			String name1 = results.getString("name");
			String position = results.getString("position");
			String email = results.getString("email");
			String phoneNumber = results.getString("phoneNumber");
			
			System.out.println("Id: "+ id + ", Name: " + name1 + ", Position: " + position + ", Email: " + email + ", Number: " + phoneNumber );
		}
	}
	public void showCompanies() throws SQLException {

		ResultSet results = showCompanies.executeQuery();

		if(results.next() == false) {
			System.out.println("You have no companies stored yet. But you can add one by entering F");
		}
		else {
			int id = results.getInt("id");
			String name1 = results.getString("companyName");
			System.out.println("Id: "+ id + ", Name: " + name1);
		}
		// Iterate over each row of the results
		while (results.next()) {
			int id = results.getInt("id");
			String name1 = results.getString("companyName");
			System.out.println("Id: "+ id + ", Name: " + name1);
		}
	}
	public void getCompanyWithMostBusinessConnections() throws SQLException {

		ResultSet results = getCompanyWithMostBusinessConnections.executeQuery();

		if(results.next() == false) {
			System.out.println("You have no companies stored yet. But you can add one by entering F");
		}
		else {
			int id = results.getInt("id");
			String name1 = results.getString("companyName");
			String count = results.getString("highestCount");
			System.out.println("Id: "+ id + ", Name: " + name1 + ", Max count: " + count);
		}
		// Iterate over each row of the results
		while (results.next()) {
			int id = results.getInt("id");
			String name1 = results.getString("companyName");
			String count = results.getString("highestCount");
			System.out.println("Id: "+ id + ", Name: " + name1 + ", Max count: " + count);
		}
	}

	public void showNotesForConnectionsById(String id) throws SQLException {

		showNotesForConnectionsById.setString(1, id);
		ResultSet results = showNotesForConnectionsById.executeQuery();

		if(results.next() == false) {
			System.out.println("There is no notes for this connection yet.");
		}
		else {
			String text = results.getString("text");
			String dateTaken = results.getString("dateTaken");
			System.out.println("Date: "+ dateTaken + "\nNote: " + text);
		}
		// Iterate over each row of the results
		while (results.next()) {
			String text = results.getString("text");
			String dateTaken = results.getString("dateTaken");
			System.out.println("Date: "+ dateTaken + "\nNote: " + text);
		}
	}
	public void showNotesForCompanyById(String id) throws SQLException {

		showNotesForCompanyById.setString(1, id);
		ResultSet results = showNotesForCompanyById.executeQuery();

		if(results.next() == false) {
			System.out.println("There is no notes for this company yet");
		}
		else {
			String text = results.getString("text");
			String dateTaken = results.getString("dateTaken");
			System.out.println("Date "+ dateTaken + "\nNote: " + text);
		}
		// Iterate over each row of the results
		while (results.next()) {
			String text = results.getString("text");
			String dateTaken = results.getString("dateTaken");
			System.out.println("Date "+ dateTaken + "\nNote: " + text);
		}
	}

	public void addCompany(String name) throws SQLException {

		addCompany.setString(1, name);
		addCompany.execute();

		System.out.println(name + " has been added.");
	}

	public void addNote(String text, String date, String connection_id, String company_id) throws SQLException {

		addNote.setString(1, text);
		addNote.setString(2, date);
		addNote.setString(3, connection_id);
		addNote.setString(4, company_id);
		addNote.execute();

		System.out.println("The note has been added.");
	}

	public void addRelationship(String connection_id, String company_id) throws SQLException {


		addRelationship.setString(1, connection_id);
		addRelationship.setString(2, company_id);
		addRelationship.execute();
		System.out.println("A new relationship is added");
	}

	public void dropConnection(String id) throws SQLException {

		dropConnection.setString(1, id);
		dropConnection.execute();
		
	}

	public void dropCompany(String id) throws SQLException {

		dropCompany.setString(1, id);
		dropCompany.execute();
		
	}

	public void showConnectionsByCompanyId(String id) throws SQLException {
		showConnectionsByCompanyId.setString(1, id);
		ResultSet results = showConnectionsByCompanyId.executeQuery();

		if(results.next() == false) {
			System.out.println("You have no connections connected to this Company");
		}
		else {
			String id1 = results.getString("id");
			String name1 = results.getString("name");
			String position = results.getString("position");
			String email = results.getString("email");
			String phoneNumber = results.getString("phoneNumber");
			System.out.println("Id: "+ id1 + ", Name: " + name1 + ", Position: " + position + ", Email: " + email + ", Number: " + phoneNumber);
		}
		// Iterate over each row of the results
		while (results.next()) {
			String id1 = results.getString("id");
			String name1 = results.getString("name");
			String position = results.getString("position");
			String email = results.getString("email");
			String phoneNumber = results.getString("phoneNumber");
			System.out.println("Id: "+ id1 + ", Name: " + name1 + ", Position: " + position + ", Email: " + email + ", Number: " + phoneNumber);
		}
	}

	/**
	 * Closes the connection to the database.
	 */
	@Override
	public void close() throws SQLException {
		connection.close();
	}

	/**
	 * Entry point of the application. Uses command-line parameters to override database
	 * connection settings, then invokes runApp().
	 */
	public static void main(String... args) {
		// Default connection parameters (can be overridden on command line)
		Map<String, String> params = new HashMap<>(Map.of(
				"dbname", "" + DB_NAME,
				"user", DB_USER,
				"password", DB_PASSWORD
				));

		boolean printHelp = false;

		// Parse command-line arguments, overriding values in params
		for (int i = 0; i < args.length && !printHelp; ++i) {
			String arg = args[i];
			boolean isLast = (i + 1 == args.length);

			switch (arg) {
			case "-h":
			case "-help":
				printHelp = true;
				break;

			case "-dbname":
			case "-user":
			case "-password":
				if (isLast)
					printHelp = true;
				else
					params.put(arg.substring(1), args[++i]);
				break;

			default:
				System.err.println("Unrecognized option: " + arg);
				printHelp = true;
			}
		}

		// If help was requested, print it and exit
		if (printHelp) {
			printHelp();
			return;
		}

		// Connect to the database. This use of "try" ensures that the database connection
		// is closed, even if an exception occurs while running the app.
		try (DatabaseTunnel tunnel = new DatabaseTunnel();
				BusinessConnections app = new BusinessConnections(
						"localhost", tunnel.getForwardedPort(), params.get("dbname"),
						params.get("user"), params.get("password")
						)) {

			// Run the application
			try {
				app.runApp();
			} catch (SQLException ex) {
				System.err.println("\n\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				System.err.println("SQL error when running database app!\n");
				ex.printStackTrace();
				System.err.println("\n\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			}
		} catch (IOException ex) {
			System.err.println("Error setting up ssh tunnel.");
			ex.printStackTrace();
		} catch (SQLException ex) {
			System.err.println("Error communicating with the database (see full message below).");
			ex.printStackTrace();
			System.err.println("\nParameters used to connect to the database:");
			System.err.printf("\tSSH keyfile: %s\n\tDatabase name: %s\n\tUser: %s\n\tPassword: %s\n\n",
					params.get("sshkeyfile"), params.get("dbname"),
					params.get("user"), params.get("password")
					);
			System.err.println("(Is the MySQL connector .jar in the CLASSPATH?)");
			System.err.println("(Are the username and password correct?)");
		}

	}

	private static void printHelp() {
		System.out.println("Accepted command-line arguments:");
		System.out.println();
		System.out.println("\t-help, -h          display this help text");
		System.out.println("\t-dbname <text>     override name of database to connect to");
		System.out.printf( "\t                   (default: %s)\n", DB_NAME);
		System.out.println("\t-user <text>       override database user");
		System.out.printf( "\t                   (default: %s)\n", DB_USER);
		System.out.println("\t-password <text>   override database password");
		System.out.println();
	}
}

