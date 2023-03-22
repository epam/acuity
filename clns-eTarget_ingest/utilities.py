 # 
 # This file is part of the eTarget ingest distribution (https://github.com/digital-ECMT/eTarget_ingest).
 # Copyright (C) 2017 - 2021 digital ECMT
 # 
 # This program is free software: you can redistribute it and/or modify  
 # it under the terms of the GNU General Public License as published by  
 # the Free Software Foundation, version 3.
 #
 # This program is distributed in the hope that it will be useful, but 
 # WITHOUT ANY WARRANTY; without even the implied warranty of 
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 # General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License 
 # along with this program. If not, see <http://www.gnu.org/licenses/>.
 #

from azure.storage.blob import AppendBlobService
import pymssql
import datetime
import os

class Util:
    
    def __init__(self, remotehostname, remoteusername, remotepassword, remotedbname, fileuser, filekey, logblob):
        self.append_blob_service = AppendBlobService(account_name=fileuser, account_key=filekey)
        self.conn = pymssql.connect(remotehostname, remoteusername, remotepassword, remotedbname, autocommit=True)
        self.filepath = os.path.dirname(os.path.abspath(__file__))
        if logblob is None:
            self.log=self.log
        else:
            self.log=logblob
        
    def __del__(self):
        if hasattr(self,'conn') and self.conn is not None:
            self.conn.close()
        
    def logMessage(self, message):
        try:
            print(message)
            now = datetime.datetime.now()
            # If hour changed, delete the current log, and start over
            if now.minute == 0:
                self.append_blob_service.delete_blob(self.log, 'target'+str(now.hour)+'.log')
                self.append_blob_service.create_blob(self.log, 'target'+str(now.hour)+'.log')
                
            if self.append_blob_service.exists(self.log, 'target'+str(now.hour)+'.log')==False :
                self.append_blob_service.create_blob(self.log, 'target'+str(now.hour)+'.log', if_none_match='*')
            self.append_blob_service.append_blob_from_text(self.log, 'target'+str(now.hour)+'.log', self.timestamp()+' '+message+'\n')
    
        except Exception as e:
            print(str(e))
            
    def systemStatusUpdate(self, filename, inputType, messageTime, message):
        # Delete any previous records for this file
        systemStatusDelete = self.conn.cursor()
        systemStatusDelete.execute("DELETE FROM SYSTEM_STATUS WHERE filename='"+filename+"'")

        # Update the database
        systemStatus = self.conn.cursor()
        systemStatus.execute("INSERT INTO SYSTEM_STATUS (filename, input_type, message_time, message) VALUES('"+filename+"', '"+inputType+"', '"+messageTime+"', '"+message+"')")
        
    def timestamp(self):
        return datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    
    def getConfig(self):
        # Get the config file details (database login)
        d = {}
        with open(self.filepath+"/.config") as f:
            for line in f:
                (key, val) = line.split(';',1)
                d[key] = val.replace('\n', '').strip()
            return d 
