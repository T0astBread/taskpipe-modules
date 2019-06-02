mkdir ./module_data/jplag

module_dir=$1
dir=$2
exclude=$3
language=$4

java -jar $module_dir/jplag-2.11.9-SNAPSHOT-jar-with-dependencies.jar ./content -s -S content/$dir -x $exclude -l $language -o ./module_data/jplag/parser_log.txt -r ./module_data/jplag/results

