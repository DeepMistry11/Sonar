##
## RunSonar.sh - run Sonar driver
##
## must be in course directory
## must use bash ver 4.0 or greater (for case fallthru ;& )

function usage {
  echo "Usage: $0 CMD SUBCMD"
  echo " Examples "
  echo " $0 brk [start|stop|status]"
  echo " $0 run [prod|cons] BRK=tcp://afsX:20201"

}

if [ -d amq -a -d S3 -a -d Sonar ];
then
 : ## null cmd in bash!
else
 echo ERROR E100 Not in the correct directory
 usage
 exit 2
fi

if [ "$JAVA_HOME" != "/afs/cad/linux/java8" ]; ## right java?
then
 echo ERROR E102 Incorrect Java Version please use the correct Java 
 exit 2
fi

grep QSONARPORT MyLog

/bin/printf "\n"

cmd=$1
shift

case $cmd in
brk)
  scmd=$1; shift;
  case $scmd in
  start) ;& ## fall thru
  stop)  ;& ## fall thru
  status)
     echo "valid cmd $scmd"
     sh S3/bin/S3 $scmd
     ;;
  *)
     echo "ERROR E150 invalid or null brk subcmd $scmd"
     usage
     exit 1
     ;;
  esac
  ;; ## for outer case, case0
run)
  if [ -z "$1" ];
  then
    echo E201 no subcmd for run, see usage
    usage
    exit 1
  fi
  if [ "$1" != "prod"  -a  "$1" != "cons" ];
  then
    echo E202 bad subcmd $1 see usage
    usage
    exit 1
  fi
  echo "compile"
  opwd=$(pwd -P)
  cd Sonar
  make          ## ensure TestAMQ is up to date
  echo "run"
  make $*       ## prod or cons with args
  cd $opwd 
  ;;
*)
  echo "ERROR E102 bad command, see usage"
  usage
  ;;
esac
