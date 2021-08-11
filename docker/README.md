# docker-compose for dev
## required
* install [docker-compose](https://docs.docker.com/compose/install/)
* install via homebrew
```bash
# homebrew installed
brew search 'docker-compose'
brew install docker-compose
```
* check installed successfully
```bash
# check installed 
docker-compose version
```

## run
```bash
# cd some-path/web-sys/docker
docker-compose up

# or you can run on background
docker-comopse up -d
```