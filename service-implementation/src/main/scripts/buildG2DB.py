#!/usr/bin/python

import sys, getopt, os

def main( argv ):

	# Setup and initialize primary settings
	# --------------------------------------
	logfile = '_logFile.txt'
	oraservice = '10.211.55.8'
	tablespace = 'G2'
	script = 'G2HelloWorld.sql'
	moniker = 'G2Hello'
	password = '12345678'
	username = os.environ.get( 'USER' ).upper()
	sqlplus = ''
	sysname = 'system'
	syspassword = '123456789'
	template = 'provisionDB.sql'
	scriptfolder = 'schema'
	templatefolder = 'template'
	
	# Retrieve command line arguments
	# --------------------------------
	try:
		opts, args = getopt.getopt( argv, "hs:m:p:t:u:", ["script=","moniker=","password=","tablespace=","username="] )
	except getopt.GetoptError:
		print 'buildG2DB.py -s <script> -m <moniker> -p <password> -t <tablespace> -u <username>'
		sys.exit( 2 )

	# Process command line arguments
	# -------------------------------
	for opt, arg in opts:
		if opt == '-h':
			print 'buildG2DB.py -s <script> -m <moniker> -p <password> -t <tablespace> -u <username>'
			sys.exit()
		elif opt in ("-s", "--script"):
			script = arg
		elif opt in ("-m", "--moniker"):
			moniker = arg
		elif opt in ("-p", "--password"):
			password = arg;
		elif opt in ("-t", "--tablespace"):
			tablespace = arg;
		elif opt in ("-u", "--username"):
			username = arg;
			
			
	if username is None:
		print 'Environment variable USER must be set'
		sys.exit( 2 )
	
	username = username + '_' + moniker + '_DBA'
	
	# Calculate script sources
	# -------------------------
	template_s = templatefolder + '/' + template
	script_s = scriptfolder + '/' + script
	
	# Calculate translated temporary script targets
	# ----------------------------------------------
	template_t = templatefolder + '/_' + template
	script_t = scriptfolder + '/_' + script
		
	# Display current settings
	# -------------------------
	print 'logfile', logfile
	print 'oraservice', oraservice
	print 'script', script_s
	print 'template', template_s
	print 'moniker', moniker
	print 'password', password
	print 'username', username

	# Provision initial database
	# ---------------------------
	sqlplus = 'sqlplus ' + sysname + '/' + syspassword + '@' + oraservice + ' @' + template_t
	print "sqlplus provision:", sqlplus
	
	params = ( ('{DBA_ID}', username), ('{TABLESPACE}', tablespace), ('{PASSWORD}', password))		
	translateFileUsingToken( template_s, template_t, params )
	
	if os.system( sqlplus ) == 0:
		print 'SQLPlus Execution Successful'
	else:
		print 'Problem with SQLPlus Execution'

	# Run create application database
	# --------------------------------
	sqlplus = 'sqlplus ' + username + '/' + password + '@' + oraservice + ' @' + script_t
	print "sqlplus create:", sqlplus
	
	params = ( ('{DBA_ID}', username), ('{TABLESPACE}', tablespace), ('{PASSWORD}', password))		
	translateFileUsingToken( script_s, script_t, params )
	
	if os.system( sqlplus ) == 0:
		print 'SQLPlus Execution Successful'
	else:
		print 'Problem with SQLPlus Execution'

# ------------------------------------------				
# Merges template file with provided values
# ------------------------------------------		
def translateFileUsingToken( infile, outfile, params ):
	
	if ( os.path.exists( outfile ) ):
		os.remove( outfile );
	
	with open( outfile, "wt" ) as out:
		for line in open( infile ):
			for name, value in params:
				line = line.replace( name, value )
			out.write( line )
	
	return;   

# -----------------	
# Main entry point	
# -----------------	
if __name__ == "__main__":
	main(sys.argv[1:])