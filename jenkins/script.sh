PORTAINER_URL="https://192.168.1.143:9443"
#PORTAINER_USER=<from Jenkins secrets>
#PORTAINER_PASS=<from Jenkins secrets>
STACK_NAME="plexmediahelper"


#Authenticate and get token
echo "{\"Username\":\"${PORTAINER_USER}\",\"Password\":\"${PORTAINER_PASS}\"}" > portainer_auth.json
TOKEN=$(curl -s -k -X POST ${PORTAINER_URL}/api/auth -H "Content-Type:application/json" -d @portainer_auth.json | jq -r .jwt)
echo ${TOKEN}
if [ ${TOKEN} == ''];then
  echo "No token received"
  exit 1
fi
STACK_ID=$(curl -s -k -H "Authorization: Bearer ${TOKEN}" $PORTAINER_URL/api/stacks | jq -r --arg NAME "${STACK_NAME}" '.[] | select(.Name == $NAME) | .Id')
if [ ${STACK_ID} != '' ];then
  echo "Found ID: ${STACK_ID}"
else
  echo "Could not find ID for stack ${STACK_NAME}"
  exit 1
fi
ENDPOINT_ID=$(curl -s -k -H "Authorization: Bearer ${TOKEN}" ${PORTAINER_URL}/api/stacks/${STACK_ID} | jq -r '.EndpointId')
if [ ${ENDPOINT_ID} != '' ];then
  echo "EndpointId: ${ENDPOINT_ID}"
else
  echo "Could not get the EndpointId"
  exit 1
fi
curl -s -k -H "Authorization: Bearer ${TOKEN}" ${PORTAINER_URL}/api/stacks/${STACK_ID}/file > stack_file.json

echo "This is the stack file:"
cat stack_file.json
NEW_DATE=$(echo `date`)
#sed 's/services/'"${str}"'\\nservices/' stack_file.json > update_stack_file.json
#sed 's/environment:/environment:\\n      REDEPLOY_DATE: '"${str}"'/' stack_file.json > update_stack_file.json
cat stack_file.json | jq --arg newdate "$NEW_DATE" '.StackFileContent |= sub("REDEPLOY_DATE: [^\n]+"; "REDEPLOY_DATE: \($newdate)")' > update_stack_file.json
echo "This is the updated stack file:"
cat update_stack_file.json

echo "Updating stack..."
curl -s -k -X PUT "${PORTAINER_URL}/api/stacks/${STACK_ID}?endpointId=${ENDPOINT_ID}" -H "Authorization: Bearer ${TOKEN}" -H "Content-Type: application/json" -d @update_stack_file.json

