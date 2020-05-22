## Docs

### Generate docs

sbt docs /paradox 


### Copy generated docs

sbt copyDocs


### After push to github,  publish to github

git subtree push --prefix=public/docs origin gh-pages