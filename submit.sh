#!/usr/bin/env bash
set -e

# Change USERNAME, PASSWORD
USERNAME="201250172"
PASSWORD="123456"

echo "--------Begin to Submit--------"

if [[ $USERNAME == "20xxxxxxx" || $PASSWORD == "xxxxxxxxx" ]]; then
  echo "Fail. You need to change USERNAME and PASSWORD"
  exit -1
fi

COURSE=PTC2022
MODULE=$(git rev-parse --abbrev-ref HEAD | tr '[a-z]' '[A-Z]')
WORKSPACE=$(basename $(realpath .))
FILE=submit.zip

#if [[ $(git status --porcelain) ]]; then
#  echo "Error: Git repository is dirty."
#  echo "Commit all your changes before submitting."
#  echo "Hint: run 'git status' to show changed files."
#  exit -1
#fi

# Construct assignmentId
ANTLRTMP="labN"

BRANCH=$(git symbolic-ref --short -q HEAD)
NUMBER=$(echo $BRANCH | tr -cd "[0-9]")
ID=$(echo $ANTLRTMP | sed "s/N/${NUMBER}/g")

echo "In branch: $BRANCH "
echo "Submit to assignment: $ID"


# Compress the whole folder instead of git storage only.
cd .. 
rm -f $FILE
zip -r "$FILE" $(ls -d "$WORKSPACE/.git" 2>/dev/null) > /dev/null
if [ $? -ne 0 ]; then
  echo ""
  echo "Fail to zip for submit.zip!"
else
  echo ""
  echo "generate submit.zip"
fi


# construct json for cul
TMPRAW='{"username":"XXXUSERXXX","password":"YYYPWDYYY"}'
DATARAW=$(echo $TMPRAW | sed "s/XXXUSERXXX/$USERNAME/g" | sed "s/YYYPWDYYY/$PASSWORD/g")
URL=http://47.122.3.40:28300

# extract token
RAW=$(curl "$URL/auth/login" \
  -H 'Content-Type: application/json' \
  --data-raw $DATARAW )
TOKEN=$(echo $RAW | sed 's/,/\n/g' | grep "token" | sed 's/:/\n/g' | sed '1d' | sed 's/}//g' | sed 's/\"//g')

RES=$(curl "$URL/submissions" \
  -H "Authorization: Bearer $TOKEN" \
  -F "assignmentId=$ID" \
  -F "file=@$FILE" )

FLAG0=$?

TIME=$(echo $RES | sed 's/,/\n/g' | grep "createdAt" | sed 's/:/\n/g' | sed '1d' | sed 's/}//g' | sed 's/\"//g')

if [[ $FLAG0 -ne 0 || -z $TIME ]]; then
  echo ""
  echo "T_T Commit Fail! T_T"
else
  echo ""
  echo "^v^ Commit Success! ^v^ "
fi

