package votix.services;

public class persistenceFactory {

    private PersistenceHandler ph = null;

    public PersistenceHandler createDB(String type, String dbUrl, String username, String password) throws ClassNotFoundException {

        if(type.equals("mySql")){
            ph = mysqlSingleton.getInstance( dbUrl, username, password);
        }
        else if(type.equals("fileHandling")){
            //file handling
        }
        return ph;
    }



}
