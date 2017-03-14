#!/bin/bash  
echo "OpenCV Cascade Classifier Training"

if [ $# -eq 0 ]
  then
  	echo ""
    echo "Please provide following arguments:"
    echo "$0 [PROJECT] [WIDTH] [HEIGHT] [NUMPOS] [NUMNEG]"
    exit
fi

# parameter
PROJECT=$1
WIDTH=$2
HEIGHT=$3
NUMPOS=$4
NUMNEG=$5

# open_cv bin folder
OPENCV='/usr/local/Cellar/opencv/2.4.13.1/bin'
cd "$PROJECT"

rm -r "trained"
mkdir "trained"

# create negative list
ls negative > "negatives_tmp.txt"
sed -e 's/^/negative\//' "negatives_tmp.txt" > "negatives.txt"
rm "negatives_tmp.txt"

# create binary vector
"$OPENCV/opencv_createsamples" -vec "positives.vec" -info "positives.txt" -w "$WIDTH" -h "$HEIGHT"

# trian opencv
"$OPENCV/opencv_traincascade" -data trained -vec "positives.vec" -bg "negatives.txt" -numPos "$NUMPOS" -numNeg "$NUMNEG" -w "$WIDTH" -h "$HEIGHT" -precalcValBufSize 2024 -precalcIdxBufSize 2024 -bt RAB -numThreads 12

echo ""
echo "-------->>>>"
echo "finished project $PROJECT!"