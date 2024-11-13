$Sources = (dir src/*.java -Recurse).Fullname 

javac -d out -classpath src $Sources -Xlint:unchecked
