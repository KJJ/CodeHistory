import java.util.*;

//code form found at: http://www.roseindia.net/tutorials/I18N/resource-bundle.shtml

public class Configurations extends java.util.ResourceBundle {

	String pathKeys = "repo mainBranch";
	@Override
	protected Object handleGetObject(String key) {
		if (key.equals("repo")){
			return "/Users/justin/Documents/log4j";
		}
		if (key.equals("mainBranch")){
			return "/logging/log4j/trunk";
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Enumeration getKeys() {
		StringTokenizer key = new StringTokenizer(pathKeys);
        return key;
	}

}
