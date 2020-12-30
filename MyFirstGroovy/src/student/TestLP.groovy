package student

def PCF_CREDS
def CLITool
def foundation
def org
def space
def domain
def appName
def vanityDomain
def vanityHost

def runBlueGreen(def env){
	if(env == "LP") {
		def result1
		def result2
		result1 = runBlueGreen_LP_Environment("LP1")
		if(result1 == "ok") {
			result2 = runBlueGreen_LP_Environment("LP2")
		} else {
			lpCleanup("edc2")
		}
		if(result2 == "ok") {
			lpCleanup("both")
		} else {
			lpCleanup("edc1")
		}
	}else {
		runBlueGreen_Other_Environments(env)
	}
}

def lpCleanup(def location) {
	try {
		if(location == "edc1") {
			echo "Rollback edc1 using Blue ... "
			rollbackUsingBlue("LP1")
		}else if(location == "edc2"){
			status = loginBasedOnEnvironment("LP2")
			echo "Clean up for EDC2 ... "
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}-GREEN"
			//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
			//sh "${CLITool}/cf logout"
		}else {
			rollbackUsingBlue("LP1")
			rollbackUsingBlue("LP2")
		}
	} catch(Exception e){
		echo "Exception occured when trying to login: ${e}"
		throw e
	}finally {
	}
}

def rollbackUsingBlue(def env) {
	status = loginBasedOnEnvironment(env)
	//sh "${CLITool}/cf rename ${appName} ${appName}-GREEN"
	//sh "${CLITool}/cf map-route ${appName}-BLUE ${domain} --hostname ${appName}"
	//sh "${CLITool}/cf map-route ${appName}-BLUE ${vanityDomain} --hostname ${vanityHost}"
	//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
	//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${vanityDomain} --hostname ${vanityHost}"
	//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
	//sh "${CLITool}/cf rename ${appName}-BLUE ${appName} && exit 1"
	//sh "${CLITool}/cf logout"
}

def runBlueGreen_LP_Environment(def env) {
	def status = loginBasedOnEnvironment(env)
	def okPosition
	try {
		okPosition = "1"
		////sh "${CLITool}/cf start ${appName}-GREEN"
		okPosition = "2"
		//sh "${CLITool}/cf map-route ${appName}-GREEN ${domain} --hostname ${appName}"
		okPosition = "3"
		//sh "${CLITool}/cf map-route ${appName}-GREEN ${vanityDomain} --hostname ${vanityHost}"
		okPosition = "4"
		//sh "${CLITool}/cf rename ${appName} ${appName}-BLUE"
		okPosition = "5"
		//sh "${CLITool}/cf rename ${appName}-GREEN ${appName}"
		okPosition = "6"
		//sh "${CLITool}/cf unmap-route ${appName} ${domain} --hostname ${appName}-GREEN"
		okPosition = "succeed"
	} catch(Exception e){
		echo "Exception occured in bluegreen process: ${e}"
		throw e
	} finally {
		echo "time to rollback. Current ok position is: " + okPosition
		switch (okPosition) {
			case "1":
			case "2":
			echo "case when there is only green instance exists ... "
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}-GREEN"
			//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
			break;
			case "3":
			echo "case when 3 fails ... "
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}-GREEN"
			//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
			//sh "${CLITool}/cf delete-orphaned-routes -f"
			break;
			case "4":
			echo "case when rename to blue is not allowed ... "
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${vanityDomain} --hostname ${vanityHost}"
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}-GREEN"
			//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
			//sh "${CLITool}/cf delete-orphaned-routes -f"
			break;
			case "5":
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${vanityDomain} --hostname ${vanityHost}"
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
			//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}-GREEN"
			//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
			//sh "${CLITool}/cf delete-orphaned-routes -f"
			//sh "${CLITool}/cf rename ${appName}-BLUE ${appName} && exit 1"
			break;
			default:
			echo "default ... "
		}
	}
	//sh "${CLITool}/cf logout"
	return "ok"
}



def runBlueGreen_Other_Environments(def env) {
	def status = loginBasedOnEnvironment(env)
	try {

		status = "1"
		////sh "${CLITool}/cf start ${appName}-GREEN"
		status = "2"
		echo "testing exception: start ..."
		//			//sh "${CLITool}/cf rezzz ${appName}-GREEN ${appName}"
		echo "testing exception: ends ..."
		//sh "${CLITool}/cf map-route ${appName}-GREEN ${domain} --hostname ${appName}"

		status = "4"
		//sh "${CLITool}/cf rename ${appName} ${appName}-BLUE"
		status = "5"
		//sh "${CLITool}/cf rename ${appName}-GREEN ${appName}"
		status = "6"
		//sh "${CLITool}/cf unmap-route ${appName} ${domain} --hostname ${appName}-GREEN"
		status = "succeed"
	} catch(Exception e){
		echo "Exception occured in bluegreen process: ${e}"
		throw e
	} finally {
		if(status == "succeed") {
			echo " time to delete ..."
			//sh "${CLITool}/cf unmap-route ${appName}-BLUE ${domain} --hostname ${appName}"
			if(env == "LP1" || env == "LP2") {
				//sh "${CLITool}/cf unmap-route ${appName}-BLUE ${vanityDomain} --hostname ${vanityHost}"
			}
			//sh "${CLITool}/cf delete ${appName}-BLUE  -r -f"
			//sh "${CLITool}/cf delete-orphaned-routes -f"
		}else {
			echo "time to rollback ... Status: " + status
			switch (status) {
				case "login":
				case "1":
				case "2":
				echo "case when there is only green instance exists ... "
				//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
				//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
				break;
				case "4":
				echo "case when rename to blue is not allowed ... "
				//sh "${CLITool}/cf delete ${appName}-BLUE -r -f"
				//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
				//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
				break;
				case "3":
				echo "case when 3 fails ... "
				//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
				//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
				//sh "${CLITool}/cf delete-orphaned-routes -f"

				break;
				case "5":
				echo "case when blue green switch fails ... "
				//sh "${CLITool}/cf map-route ${appName}-BLUE ${domain} --hostname ${appName}"
				//sh "${CLITool}/cf unmap-route ${appName}-GREEN ${domain} --hostname ${appName}"
				//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
				//sh "${CLITool}/cf rename ${appName}-BLUE ${appName} && exit 1"
				break;
				case "6":
				echo "case when blue green switch fails ... "
				//sh "${CLITool}/cf map-route ${appName}-BLUE ${domain} --hostname ${appName}"
				//sh "${CLITool}/cf unmap-route ${appName} ${domain} --hostname ${appName}"
				//sh "${CLITool}/cf delete ${appName} -r -f"
				//sh "${CLITool}/cf delete ${appName}-GREEN -r -f"
				//sh "${CLITool}/cf rename ${appName}-BLUE ${appName} && exit 1"
				break;
				default:
				echo "default ... "
			}
		}
		//sh "${CLITool}/cf logout"
	}
}

def loginBasedOnEnvironment(def env) {
	def status
	echo "The environment is: " + env
	PCF_CREDS = credentials('696cb216-87ff-46df-9c56-28fec7297a67')
	CLITool = "$HOME/tools/com.cloudbees.jenkins.plugins.customtools.CustomTool/cf_cli_6_28_0"

	switch (env) {
		case "QA":
		appName = "globalesb-wlpqa"
		foundation = "https://api.sys.pp01.edc2.cf.ford.com"
		domain = "apps.pp01i.edc2.cf.ford.com"
		org = "GESB_EDC2_Preprod"
		space = "GESB_QA"
		break;
		case "DEV":
		appName = "globalesb-wlp"
		foundation = "https://api.sys.pp01.edc2.cf.ford.com"
		domain = "apps.pp01i.edc2.cf.ford.com"
		org = "GESB_EDC2_Preprod"
		space = "GESB_DEV"
		break;
		case "EDU":
		appName = "gesbedu"
		foundation = "https://api.sys.pp01.edc1.cf.ford.com"
		domain = "apps.pp01i.edc1.cf.ford.com"
		org = "GESB_EDC1_Preprod"
		space = "GESB_EDU"
		break;
		case "LP1":
		appName = "gesblp"
		foundation = "https://api.sys.pp01.edc1.cf.ford.com"
		domain = "apps.pp01i.edc1.cf.ford.com"
		org = "GESB_EDC1_Preprod"
		space = "GESB_LP"
		vanityDomain = "gesbcontracts.ford.com"
		vanityHost = "wwwlp"
		break;
		case "LP2":
		appName = "gesblp"
		foundation = "https://api.sys.pp01.edc2.cf.ford.com"
		domain = "apps.pp01i.edc2.cf.ford.com"
		org = "GESB_EDC2_Preprod"
		space = "GESB_LP"
		vanityDomain = "gesbcontracts.ford.com"
		vanityHost = "wwwlp"
		break;
		default:
		appName = "globalesb-wlpqa"
		foundation = "https://api.sys.pp01.edc2.cf.ford.com"
		domain = "apps.pp01i.edc2.cf.ford.com"
		org = "GESB_EDC2_Preprod"
		space = "GESB_QA"
	}
	status = true
	try {
		//sh "${CLITool}/cf login --skip-ssl-validation -a ${foundation} -u ${PCF_CREDS_USR} -p ${PCF_CREDS_PSW} -o ${org} -s ${space}"
	} catch(Exception e){
		echo "Exception occured when trying to login: ${e}"
		status = false
		throw e
	}finally {
		return status
	}
}

return this
