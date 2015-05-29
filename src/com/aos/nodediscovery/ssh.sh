remoteuser=rkn130030
remotecomputer01=dc01.utdallas.edu
remotecomputer02=dc02.utdallas.edu
remotecomputer03=dc03.utdallas.edu


ssh -l "$remoteuser" "$remotecomputer01" 
ssh -l "$remoteuser" "$remotecomputer02"
ssh -l "$remoteuser" "$remotecomputer03"
