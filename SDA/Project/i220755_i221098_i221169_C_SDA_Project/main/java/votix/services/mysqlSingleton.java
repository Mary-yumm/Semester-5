package votix.services;

import javafx.scene.image.Image;
import votix.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mysqlSingleton extends PersistenceHandler {

    private Connection conn;
    private String dbUrl;
    private String username;
    private String password;
    private String databaseName;
    private boolean isConnected;
    private int connectionTimeout;
    private int maxRetries;

    private static mysqlSingleton instance = null;

    private mysqlSingleton(String dbUrl, String username, String password) throws ClassNotFoundException {
        // replace with your password
        try {
            // Optional: Load the SQL Server JDBC driver
            // Class.forName("org.mariadb.jdbc.Driver");

            // Establish the connection
            conn = DriverManager.getConnection(dbUrl, username, password);
            if (conn != null) {
                System.out.println("Connection established successfully.");
            } else {
                System.out.println("Failed to establish connection.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to establish connection.");
            e.printStackTrace();
        }
    }

    public static mysqlSingleton getInstance(String dbUrl, String username, String password) throws ClassNotFoundException {
        if(instance == null){
            instance = new mysqlSingleton(dbUrl,username, password);
            System.out.println(dbUrl+", "+username+", "+password);
        }
        return instance;
    }



    @Override
    public ArrayList<ElectionResult> getForm(int sID , String areaName, String Napa) {

        ArrayList<ElectionResult> temp = new ArrayList<>();
        String query = "SELECT " +
                "    CANDIDATE.name, " +
                "    CANDIDATE.partyName, " +
                "    ELECTIONRESULT.voteCount " +
                "FROM " +
                "    votix.ELECTIONRESULT " +
                "JOIN " +
                "    AREA ON ELECTIONRESULT.areaId = AREA.areaId " +
                "JOIN " +
                "    CANDIDATE ON ELECTIONRESULT.candidateId = CANDIDATE.candidateId " +
                "JOIN " +
                "    POLLINGSTATION ON AREA.areaId = POLLINGSTATION.areaId " +
                "WHERE " +
                "    AREA.areaName LIKE ? " +
                "    AND CANDIDATE.naPa LIKE ? " +
                "    AND POLLINGSTATION.stationId = ? ;";

    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, "%" + areaName + "%");
        stmt.setString(2, "%" + Napa + "%");
        stmt.setInt(3, sID);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            temp.add(new ElectionResult(
                    areaName,
                    rs.getString("name"),
                    rs.getString("partyName"),
                    rs.getInt("voteCount")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return temp;

    }

    @Override
    public ArrayList<Integer> getStations(String areaName){
        ArrayList<Integer> temp = new ArrayList<>();

        // Query to fetch election results, joining AREA and CANDIDATE tables
        String query = "SELECT stationId "
                + "FROM votix.POLLINGSTATION "
                + "JOIN AREA ON POLLINGSTATION.areaId = AREA.areaId "
                + "WHERE areaName LIKE ? ";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + areaName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                temp.add(rs.getInt("stationId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public ArrayList<ElectionResult> WinnerByArea(String areaName, String napa)
    {
        ArrayList<ElectionResult> temp = new ArrayList<>();

        // Query to fetch election results, joining AREA and CANDIDATE tables
        String query = "SELECT AREA.areaName, CANDIDATE.name, CANDIDATE.partyName, ELECTIONRESULT.voteCount "
                + "FROM votix.ELECTIONRESULT "
                + "JOIN AREA ON ELECTIONRESULT.areaId = AREA.areaId "
                + "JOIN CANDIDATE ON ELECTIONRESULT.candidateId = CANDIDATE.candidateId "
                + "WHERE areaName LIKE ? "
                + "AND CANDIDATE.naPa LIKE ? "
                + "ORDER BY ELECTIONRESULT.voteCount DESC "
                + "LIMIT 2";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, "%" + areaName + "%");
                stmt.setString(2, "%" + napa + "%");

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    temp.add(new ElectionResult(
                            rs.getString("areaName"),
                            rs.getString("name"),
                            rs.getString("partyName"),
                            rs.getInt("voteCount")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return temp;
    }

    public int fetchTotalVotesByArea(String areaName) {

        int totalVotes = 0; // Initialize total votes to 0
        try {
            // SQL query to fetch the total votes for areas matching 'Islamabad'
            String query = "SELECT SUM(ELECTIONRESULT.voteCount) AS totalVotes " +
                    "FROM votix.ELECTIONRESULT " +
                    "JOIN AREA ON ELECTIONRESULT.areaId = AREA.areaId " +
                    "WHERE AREA.areaName LIKE ? " +
                    "GROUP BY ELECTIONRESULT.areaId";

            // Prepare the statement
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + areaName + "%");

            // Execute the query and retrieve results
            ResultSet rs = ps.executeQuery();

            // Check if the result set has at least one row
            if (rs.next()) {
                // Retrieve the total votes from the result set
                totalVotes = rs.getInt("totalVotes");
            }
        } catch (SQLException e) {
            // Print the exception for debugging
            e.printStackTrace();
        }
        return totalVotes; // Return the total votes
    }

    @Override
    public ArrayList<ElectionResult> fetchElectionResults(String napa) {
            ArrayList<ElectionResult> electionResults = new ArrayList<>();
        System.out.println("In mysql Input napa: " + napa);

        try {
                // Query to fetch election results, joining AREA and CANDIDATE tables
                String electionQuery = "SELECT AREA.areaName, CANDIDATE.name, CANDIDATE.partyName, ELECTIONRESULT.voteCount "
                        + "FROM votix.ELECTIONRESULT "
                        + "JOIN AREA ON ELECTIONRESULT.areaId = AREA.areaId "
                        + "JOIN CANDIDATE ON ELECTIONRESULT.candidateId = CANDIDATE.candidateId "
                        + "WHERE CANDIDATE.naPa LIKE ? ";

                // Prepare the SQL statement
                PreparedStatement electionPs = conn.prepareStatement(electionQuery);
                electionPs.setString(1, "%" + napa + "%");

                // Execute the query
                ResultSet electionRs = electionPs.executeQuery();

                // Process the result set
                while (electionRs.next()) {
                    String areaName = electionRs.getString("areaName");
                    String candidateName = electionRs.getString("name");
                    String partyName = electionRs.getString("partyName");
                    int voteCount = electionRs.getInt("voteCount");

                    // Create ElectionResult object and set the values
                    ElectionResult electionResult = new ElectionResult();
                    electionResult.setAreaName(areaName);
                    electionResult.setCandidateName(candidateName);
                    electionResult.setPartyName(partyName);
                    electionResult.setVoteCount(voteCount);

                    // Add the election result to the list
                    electionResults.add(electionResult);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return electionResults;
        }

    public ArrayList<ElectionResult> searchByArea(String areaName) {
        ArrayList<ElectionResult> results = new ArrayList<>();
        String query = "SELECT areaName, name, partyName, voteCount "
                + "FROM votix.ELECTIONRESULT "
                + "JOIN AREA ON ELECTIONRESULT.areaId = AREA.areaId "
                + "JOIN CANDIDATE ON ELECTIONRESULT.candidateId = CANDIDATE.candidateId "
                + "WHERE areaName LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + areaName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new ElectionResult(
                        rs.getString("areaName"),
                        rs.getString("name"),
                        rs.getString("partyName"),
                        rs.getInt("voteCount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public ArrayList<ElectionResult> searchByCandidate(String candidateName) {
        ArrayList<ElectionResult> results = new ArrayList<>();
        String query = "SELECT areaName, name, partyName, voteCount "
                + "FROM votix.ELECTIONRESULT "
                + "JOIN AREA ON ELECTIONRESULT.areaId = AREA.areaId "
                + "JOIN CANDIDATE ON ELECTIONRESULT.candidateId = CANDIDATE.candidateId "
                + "WHERE name LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + candidateName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new ElectionResult(
                        rs.getString("areaName"),
                        rs.getString("name"),
                        rs.getString("partyName"),
                        rs.getInt("voteCount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public ArrayList<ElectionResult> searchByParty(String partyName) {
        ArrayList<ElectionResult> results = new ArrayList<>();
        String query = "SELECT areaName, name, partyName, voteCount "
                + "FROM votix.ELECTIONRESULT "
                + "JOIN AREA ON ELECTIONRESULT.areaId = AREA.areaId "
                + "JOIN CANDIDATE ON ELECTIONRESULT.candidateId = CANDIDATE.candidateId "
                + "WHERE partyName LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + partyName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new ElectionResult(
                        rs.getString("areaName"),
                        rs.getString("name"),
                        rs.getString("partyName"),
                        rs.getInt("voteCount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public ArrayList<Candidate> fetchCandidates(int areaId) {
        ArrayList<Candidate> candidates = new ArrayList<>();
        try {
            // SQL query to fetch candidates that belong to the specified areaId
            String query = "SELECT CANDIDATE.* FROM CANDIDATE "
                    + "INNER JOIN CANDIDATE_AREA ON CANDIDATE.candidateId = CANDIDATE_AREA.candidateId "
                    + "WHERE CANDIDATE_AREA.areaId = ?";

            // Prepare the statement
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, areaId);  // Set the areaId parameter

            // Execute the query and retrieve results
            ResultSet rs = ps.executeQuery();

            // Loop through the result set to create Candidate objects
            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCid(rs.getInt("candidateId"));
                candidate.setName(rs.getString("name"));
                candidate.setPartyName(rs.getString("partyName"));

                // Retrieve the party symbol name from the database
                String partySymbolFile = rs.getString("partySymbol") + ".png";

                // Construct the full path for the party symbol image
                String imagePath = "/assets/partySymbols/" + partySymbolFile;

                // Load the image if the file exists
                try {
                    candidate.setPartySymbol(new Image(getClass().getResource(imagePath).toExternalForm()));
                } catch (NullPointerException e) {
                    System.err.println("Image not found for path: " + imagePath);
                    // Optionally set a default image
                    candidate.setPartySymbol(new Image(getClass().getResource("/assets/partySymbols/default.png").toExternalForm()));
                }

                candidate.setRegistrationDate(rs.getDate("registrationDate"));
                candidate.setNAPA(rs.getString("naPa"));

                // Add the candidate to the list
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }

    @Override
    public List<Object[]> getCandidateVotes() {
        String query = " SELECT candidateId, voteCount FROM ELECTIONRESULT";

        List<Object[]> resultList = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int candidateId = rs.getInt("candidateId");
                int voteCount = rs.getInt("voteCount");

                // Check if candidate already exists in the list
                boolean found = false;
                for (Object[] entry : resultList) {
                    if ((int) entry[0] == candidateId) {
                        entry[1] = (int) entry[1] + voteCount;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    resultList.add(new Object[] { candidateId, voteCount });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;  // Return the list of distinct candidates with total votes
    }

    @Override
        public ArrayList<Object> getStaffAssignments() {

            String query = """
                SELECT 
                    PS.staffId,
                    PS.name AS StaffName,
                    PS.stationId,
                    P.areaId,
                    A.areaName
                FROM 
                    POLLINGSTAFF PS
                JOIN 
                    POLLINGSTATION P ON PS.stationId = P.stationId
                JOIN 
                    AREA A ON P.areaId = A.areaId;
                """;

            ArrayList<Object> staffList = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int staffId = rs.getInt("staffId");
                    String staffName = rs.getString("StaffName");
                    int stationId = rs.getInt("stationId");
                    int areaId = rs.getInt("areaId");
                    String areaName = rs.getString("areaName");

                    // Create an Object array and add it to the staffList
                    Object[] staffDetails = new Object[] { staffId, staffName, stationId, areaId, areaName };
                    staffList.add(staffDetails);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return staffList; // Return the list of staff details
        }

        @Override   //returns all the staff members
    public ArrayList<PollingStaff> getStaffList() {

            ArrayList<PollingStaff> staffList = new ArrayList<>();
            try {
                String staffQuery = "SELECT * FROM POLLINGSTAFF";
                PreparedStatement staffPs = conn.prepareStatement(staffQuery);
                ResultSet staffRs = staffPs.executeQuery();

                while (staffRs.next()) {
                    PollingStaff staff = new PollingStaff();
                    staff.setStaffID(staffRs.getInt("staffId"));
                    staff.setName(staffRs.getString("name"));
                    staff.setAssignedStation(staffRs.getInt("stationId"));
                    staff.setRole(staffRs.getString("role"));
                    staff.setUsername(staffRs.getString("username"));
                    staff.setPassword(staffRs.getString("password"));

                    staffList.add(staff);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
                return staffList;

        }
    @Override
    public List<PollingStationPC> getPollingPCs() {
        List<PollingStationPC> pollingPCs = new ArrayList<>();
        String query = """
        SELECT pc.systemId, pc.stationId, pc.systemStatus, pc.config, a.areaName
        FROM POLLINGSTATIONPC pc
        JOIN POLLINGSTATION ps ON pc.stationId = ps.stationId
        JOIN AREA a ON ps.areaId = a.areaId
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PollingStationPC pc = new PollingStationPC();
                pc.setSystemID(rs.getString("systemId"));
                pc.setStationID(rs.getInt("stationId"));
                pc.setSystemStatus("Active".equals(rs.getString("systemStatus")));
                pc.setConfigurationSettings(rs.getString("config"));
                pc.setAreaName(rs.getString("areaName"));

                pollingPCs.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception
        }

        return pollingPCs;
    }

    @Override
    public int verifyStaff(String username, String password, String currentMac) {
        try {
            // Step 1: Verify staff credentials in the POLLINGSTAFF table
            String query = "SELECT stationId FROM POLLINGSTAFF WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            // If no matching staff is found, return 0
            if (!rs.next()) {
                System.out.println("Invalid credentials.");
                return 0;
            }

            // Get the stationId from the POLLINGSTAFF table
            int stationId = rs.getInt("stationId");

            // Step 2: Verify the MAC address and return the system ID if successful
            int systemId = verifyMacAddress(stationId, currentMac);
            if (systemId > 0) {
                return stationId; // Return system ID if MAC address verification succeeds
            }

            System.out.println("MAC address mismatch or station unauthorized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if verification fails
    }

    /**
     * Verifies if the provided MAC address matches the configuration for the given stationId.
     *
     * @param stationId  The ID of the station to verify.
     * @param currentMac The MAC address of the current PC.
     * @return The system ID if the MAC address matches; 0 otherwise.
     */
    @Override
    public int verifyMacAddress(int stationId, String currentMac) {
        try {
            if (currentMac == null) {
                System.out.println("Unable to retrieve MAC address.");
                return 0;
            }

            // Convert the current MAC address to lowercase for case-insensitive comparison
            currentMac = currentMac.toLowerCase();

            // Query the POLLINGSTATIONPC table for the stationId's MAC address configuration
            String query = "SELECT systemId, config FROM POLLINGSTATIONPC WHERE stationId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, stationId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String configMac = rs.getString("config");

                // Convert the config MAC address to lowercase for comparison
                if (configMac != null && configMac.toLowerCase().equals(currentMac)) {
                    return rs.getInt("systemId"); // Return the system ID if MAC address matches
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if MAC address verification fails
    }




    @Override
    public int verifyAdmin(String username, String password) {
        String query = "SELECT adminID FROM ADMIN WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int adminID = rs.getInt("adminID"); // Retrieve adminID
                    System.out.println("Admin " + adminID + " logged in");
                    return 1;
                } else {
                    System.out.println("Invalid credentials.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean isVoterRegistered(String name, String cnic, int areaid) {
        boolean isRegistered = false;
        try {
            // First, find all the stationIds for the given areaId
            String stationQuery = "SELECT stationId FROM POLLINGSTATION WHERE areaId = ?";
            PreparedStatement stationStmt = conn.prepareStatement(stationQuery);
            stationStmt.setInt(1, areaid); // Set the areaId as parameter
            ResultSet stationRs = stationStmt.executeQuery();

            // Fetch all stationIds associated with the areaId
            ArrayList<Integer> stationIds = new ArrayList<>();
            while (stationRs.next()) {
                stationIds.add(stationRs.getInt("stationId"));
            }

            // Now, check if any of the stationIds are associated with the given voter
            for (int stationId : stationIds) {
                String query = "SELECT * FROM VOTER WHERE cnic = ? AND name = ? AND stationId = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, cnic);  // Set CNIC as parameter
                ps.setString(2, name);  // Set Name as parameter
                ps.setInt(3, stationId); // Set the current stationId for checking

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    isRegistered = true;
                    break; // If a match is found, no need to check further
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isRegistered;
    }
    @Override
    public void updatePollingStaffAccount(String username, String password, int staffid, int stationid) {
        try {
            String query = "UPDATE POLLINGSTAFF SET username = ?, password = ?, stationId = ? WHERE staffId = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, stationid);
            ps.setInt(4, staffid);

            int res = ps.executeUpdate();

            if (res == 0) {
                System.out.println("No matching record found for staffId " + staffid);
            } else {
                System.out.println("data updated successfully for staffId " + staffid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void activatePollingStaffAccount(int id) {
        try {
            String query = "UPDATE POLLINGSTAFF SET status = 'ACTIVE' WHERE staffId = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, id);

            int res = ps.executeUpdate();

            if (res == 0) {
                System.out.println("No matching record found for staffId " + id);
            } else {
                System.out.println("status updated successfully for staffId " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSystemInactive(int systemID) {
        // SQL query to update system status to 'Inactive' based on systemID
        String query = "UPDATE POLLINGSTATIONPC SET systemStatus = 'Inactive' WHERE systemId = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            // Set the systemID in the query
            ps.setInt(1, systemID);

            // Execute the update
            int rowsUpdated = ps.executeUpdate();

            // Check if any row was updated
            if (rowsUpdated == 0) {
                System.out.println("No system found with systemId: " + systemID);
            } else {
                System.out.println("System " + systemID + " has been set to Inactive.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSystemActive(int systemID) {
        // SQL query to update system status to 'Active' based on systemID
        String query = "UPDATE POLLINGSTATIONPC SET systemStatus = 'Active' WHERE systemId = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            // Set the systemID in the query
            ps.setInt(1, systemID);

            // Execute the update
            int rowsUpdated = ps.executeUpdate();

            // Check if any row was updated
            if (rowsUpdated == 0) {
                System.out.println("No system found with systemId: " + systemID);
            } else {
                System.out.println("System " + systemID + " has been set to Active.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String fetchAreaName(int areaID) {
        // SQL query to fetch areaName using areaID
        String query = "SELECT areaName FROM AREA WHERE areaId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, areaID); // Set the areaID parameter

            // Execute the query and retrieve the result
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Return the areaName if found
                return rs.getString("areaName");
            } else {
                // Return a value indicating no area found
                return "Area not found";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the error or return a default value
            return "Error fetching area name";
        }
    }


    @Override
    public void deactivatePollingStaffAccount(int  staffid) {
        try {
            String query = "UPDATE POLLINGSTAFF SET status = 'INACTIVE' WHERE staffId = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, staffid);

            int res = ps.executeUpdate();

            if (res == 0) {
                System.out.println("No matching record found for staffId " + staffid);
            } else {
                System.out.println("status updated successfully for staffId " + staffid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int fetchArea(int stationId) {
        // SQL query to fetch areaId using stationId
        String query = "SELECT areaId FROM POLLINGSTATION WHERE stationId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, stationId);  // Set the stationId parameter

            // Execute the query and retrieve the result
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Return the areaId if found
                return rs.getInt("areaId");
            } else {
                // Return a value indicating no area found, you can throw an exception or return -1
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 or handle error as needed
        }

    }

    @Override
    public void updateVoteCount(int candid, int areaid) {
        try {
            // SQL query to update vote count for the given candidate and area
            String query = "UPDATE ELECTIONRESULT SET voteCount = voteCount + 1 WHERE candidateId = ? AND areaId = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            // Set the parameters for candidateId and areaId
            ps.setInt(1, candid);
            ps.setInt(2, areaid);

            // Execute the update
            int rowsUpdated = ps.executeUpdate();

            // If no rows were updated, candidate and area might not exist
            if (rowsUpdated == 0) {
                System.out.println("No matching record found for candidateId " + candid + " in areaId " + areaid);
            } else {
                System.out.println("Vote count updated successfully for candidateId " + candid + " in areaId " + areaid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public ArrayList<PollingStation> fetchStations(int areaID) {
        ArrayList<PollingStation> pollingStations = new ArrayList<>();

        try {
            // Query to fetch stations by areaID
            String stationQuery = "SELECT * FROM POLLINGSTATION WHERE areaId = ?";
            PreparedStatement stationPs = conn.prepareStatement(stationQuery);
            stationPs.setInt(1, areaID);
            ResultSet stationRs = stationPs.executeQuery();

            while (stationRs.next()) {
                int stationId = stationRs.getInt("stationId");

                // Create PollingStation object
                PollingStation pollingStation = new PollingStation();
                pollingStation.setStationID(stationId);

                // Fetch and set PollingStaff for this station
                ArrayList<PollingStaff> staffList = fetchPollingStaff(stationId);
                pollingStation.setAssignedPollingStaff(staffList);

                // Fetch and set PollingStationPCs for this station
                ArrayList<PollingStationPC> pcList = fetchPollingStationPCs(stationId);
                pollingStation.setPollingPCs(pcList);

                // Fetch and set Voters for this station
                ArrayList<Voter> votersList = fetchVoters(stationId);
                pollingStation.setVoters(votersList);

                pollingStations.add(pollingStation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pollingStations;
    }


    @Override
    public void changeVoterStatus(String cnic) {
        String sql = "UPDATE VOTER SET status = TRUE WHERE cnic = ?";

        try  // Assuming you have a Database class for connection
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, cnic);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Voter status updated to true for CNIC: " + cnic);
            } else {
                System.out.println("No voter found with CNIC: " + cnic);
            }
        } catch (SQLException e) {
            System.out.println("Error updating voter status: " + e.getMessage());
        }
    }

    // Helper method to fetch PollingStaff by stationId
    private ArrayList<PollingStaff> fetchPollingStaff(int stationId) throws SQLException {
        ArrayList<PollingStaff> staffList = new ArrayList<>();

        String staffQuery = "SELECT * FROM POLLINGSTAFF WHERE stationId = ?";
        PreparedStatement staffPs = conn.prepareStatement(staffQuery);
        staffPs.setInt(1, stationId);
        ResultSet staffRs = staffPs.executeQuery();

        while (staffRs.next()) {
            PollingStaff staff = new PollingStaff();
            staff.setStaffID(staffRs.getInt("staffId"));
            staff.setName(staffRs.getString("name"));
            staff.setAssignedStation(staffRs.getInt("stationId"));
            staff.setRole(staffRs.getString("role"));
            staff.setUsername(staffRs.getString("username"));
            staff.setPassword(staffRs.getString("password"));

            staffList.add(staff);
        }
        return staffList;
    }

    // Helper method to fetch PollingStationPCs by stationId
    private ArrayList<PollingStationPC> fetchPollingStationPCs(int stationId) throws SQLException {
        ArrayList<PollingStationPC> pcList = new ArrayList<>();

        String pcQuery = "SELECT * FROM POLLINGSTATIONPC WHERE stationId = ?";
        PreparedStatement pcPs = conn.prepareStatement(pcQuery);
        pcPs.setInt(1, stationId);
        ResultSet pcRs = pcPs.executeQuery();

        while (pcRs.next()) {
            PollingStationPC pc = new PollingStationPC();
            pc.setSystemID(pcRs.getString("systemId"));
            pc.setStationID(pcRs.getInt("stationId"));
            pc.setSystemStatus("Active".equals(pcRs.getString("systemStatus")));
            pc.setConfigurationSettings(pcRs.getString("config"));

            pcList.add(pc);
        }
        return pcList;
    }

    // Method to fetch voters for a specific polling station
    private ArrayList<Voter> fetchVoters(int stationID) {
        ArrayList<Voter> votersList = new ArrayList<>();
        String voterQuery = "SELECT * FROM VOTER WHERE stationId = ?";

        try (PreparedStatement voterPs = conn.prepareStatement(voterQuery)) {
            voterPs.setInt(1, stationID);
            ResultSet voterRs = voterPs.executeQuery();

            while (voterRs.next()) {
                String cnic = voterRs.getString("cnic");
                String name = voterRs.getString("name");
                String gender = voterRs.getString("gender");
                boolean status = voterRs.getBoolean("status");

                // Assuming you have a Voter class with a constructor that takes these parameters
                Voter voter = new Voter(cnic, name, gender, status);
                System.out.println("voterrr: "+voter.getStatus());
                votersList.add(voter);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return votersList;
    }

public ArrayList<Integer> getStations(){
        ArrayList<Integer> list = new ArrayList<>();

        try {

        String query = "SELECT stationId FROM POLLINGSTATION ";

        PreparedStatement ps = conn.prepareStatement(query);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int sid= rs.getInt("stationId");
            list.add(sid);
        }
            return list;

    } catch (SQLException e) {
        e.printStackTrace();
    }
        return list;
}

    @Override
    public Voter getVoterByCnic(String cnic) {
        Voter voter = null;
        String query = "SELECT * FROM VOTER WHERE cnic = ?"; // Using parameterized query to prevent SQL injection

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set the CNIC parameter
            stmt.setString(1, cnic);

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve voter details from result set
                    String name = rs.getString("name");
                    String gender = rs.getString("gender");
                    boolean status = rs.getBoolean("status");

                    // Create a new Voter object and set its properties
                    voter = new Voter(cnic, name, gender, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle exception as needed
        }

        return voter; // Return the found voter or null if not found
    }


    public ArrayList<String> fetchResult()
    {
        ArrayList<String> partyNames = new ArrayList<>();
        String partyQuery = "SELECT distinct partyName FROM CANDIDATE";

        try (PreparedStatement voterPs = conn.prepareStatement(partyQuery)) {
            ResultSet partyRs = voterPs.executeQuery();

            while (partyRs.next()) {
                String pname = partyRs.getString("partyName");
                partyNames.add(pname);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partyNames;
    }


    @Override
    public ArrayList<String> reportData() {
        return null;
    }

    @Override
    public ArrayList<String> getElectionForm() {
        return null;
    }

    @Override
    public ArrayList<String> electionReportData() {
        return null;
    }


    @Override
    public void createLog(String message) {
        String sql = "INSERT INTO AUDITLOG (action, timeStamp) VALUES (?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message);
            pstmt.executeUpdate();
            System.out.println("Log entry successfully added: " + message);
        } catch (SQLException e) {
            System.err.println("Failed to log message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public List<Log> ViewLogs() {
        List<Log> logs = new ArrayList<>();
        String query = "SELECT logId, action, timeStamp FROM AUDITLOG";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int logId = rs.getInt("logId");
                String action = rs.getString("action");
                String timeStamp = rs.getString("timeStamp");

                // Create a Log object and add it to the list
                logs.add(new Log(logId, action, timeStamp));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }

        return logs;
    }


    @Override
    public boolean addPollingStaffAccount(PollingStaff staff) {
        String sql = "INSERT INTO POLLINGSTAFF (staffId, stationId, name, role, username, password) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            // Set the values in the prepared statement
            preparedStatement.setInt(1, staff.getStaffID());
            preparedStatement.setInt(2, staff.getAssignedStation());
            preparedStatement.setString(3, staff.getName());
            preparedStatement.setString(4, staff.getRole());
            preparedStatement.setString(5, staff.getUsername());
            preparedStatement.setString(6, staff.getPassword());

            // Execute the update (INSERT)
            preparedStatement.executeUpdate();

            System.out.println("staff added successfully.");


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while adding staff: " + e.getMessage());
            return false;
        }
        return true;
    }


    public ArrayList<Candidate> fetchAllCandidates() {
        ArrayList<Candidate> candidates = new ArrayList<>();
        try {
            // SQL query to fetch candidates that belong to the specified areaId
            String query = "SELECT * FROM CANDIDATE ";

            // Prepare the statement
            PreparedStatement ps = conn.prepareStatement(query);
            // Execute the query and retrieve results
            ResultSet rs = ps.executeQuery();

            // Loop through the result set to create Candidate objects
            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setCid(rs.getInt("candidateId"));
                candidate.setName(rs.getString("name"));
                candidate.setPartyName(rs.getString("partyName"));
                String partySymbolFile = rs.getString("partySymbol") + ".png";

                // Construct the full path for the party symbol image
                String imagePath = "/assets/partySymbols/" + partySymbolFile;
                candidate.setPartySymbol(new Image(getClass().getResource(imagePath).toExternalForm()));

                candidate.setRegistrationDate(rs.getDate("registrationDate"));
                candidate.setNAPA(rs.getString("naPa"));

                candidates.add(candidate);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }
    @Override
    public boolean addCandidate(Candidate candidate, String area) {

        String sql = "INSERT INTO CANDIDATE (candidateId, name, partyName, registrationDate, partySymbol, naPa) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            // Set the values in the prepared statement
            preparedStatement.setInt(1, candidate.getCid());
            preparedStatement.setString(2, candidate.getName());
            preparedStatement.setString(3, candidate.getPartyName());
            preparedStatement.setDate(4, candidate.getRegistrationDate());

            //extracts the name of the symbol and adds it to the db
            String filePath =  candidate.getPartySymbolPath();
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

            preparedStatement.setString(5, baseName);

            if(candidate.getNapa().equals("National Assembly")){
                preparedStatement.setString(6, "NA");
            }
            else{
                preparedStatement.setString(6, "PA");
            }
            // Execute the update (INSERT)
            preparedStatement.executeUpdate();

            System.out.println("Candidate added successfully.");
            String sql1 = "INSERT INTO CANDIDATE_AREA (candidateId, areaId) " + "VALUES (?,?)";

            try (PreparedStatement preparedStatement1 = conn.prepareStatement(sql1)) {


                preparedStatement1.setInt(1, candidate.getCid());
                preparedStatement1.setString(2, area);


                preparedStatement1.executeUpdate();

                System.out.println("Candidate area added successfully.");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error while adding candidate area: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while adding candidate: " + e.getMessage());
            return false;
        }


    }

    @Override
    public ArrayList<String> getPartyNames(){

        ArrayList<String> partyNames = new ArrayList<>();
        String partyQuery = "SELECT distinct partyName FROM CANDIDATE";

        try (PreparedStatement voterPs = conn.prepareStatement(partyQuery)) {
            ResultSet partyRs = voterPs.executeQuery();

            while (partyRs.next()) {
                String pname = partyRs.getString("partyName");
                partyNames.add(pname);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partyNames;
    }

    public ArrayList<String> getAreaID() {
        ArrayList<String> areaIDs = new ArrayList<>();
        String areaQuery = "SELECT DISTINCT areaID FROM AREA";

        try (PreparedStatement areaPs = conn.prepareStatement(areaQuery)) {
            ResultSet areaRs = areaPs.executeQuery();

            while (areaRs.next()) {

                int areaID = areaRs.getInt("areaID");
                areaIDs.add(String.valueOf(areaID));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return areaIDs;
    }

}
