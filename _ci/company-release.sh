SOURCE_IMAGE=quay.io/alfresco/alfresco-share
TARGET_IMAGE=alfresco/alfresco-share
docker pull $SOURCE_IMAGE:$TRAVIS_BRANCH
docker tag $SOURCE_IMAGE:$TRAVIS_BRANCH $TARGET_IMAGE:$TRAVIS_BRANCH
docker push $TARGET_IMAGE:$TRAVIS_BRANCH