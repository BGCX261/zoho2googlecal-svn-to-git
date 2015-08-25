package com.incompanysolutions.ZohoSyncCalendar;


public class LogManager {

    static private LogManager LogManager = null;

    private LogManager() { }

    private String _log="";
    
    public String getLog() {
		return _log;
	}

    public boolean containsError = false;
    public boolean needsAction = false;
    
	public void setLog(String log) {
		System.out.println(log);
		this._log = _log  + log + "\n";
	}

	static public LogManager getLogManager() {

        if (LogManager == null) {
            LogManager = new LogManager();
        }
        return LogManager;
    }

    /*
     * Metodos del LogManager.
     */

    public String metodo() {
        return "LogManager instanciado bajo demanda";
    }

    
    
}