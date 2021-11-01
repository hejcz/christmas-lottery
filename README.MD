# Christmas Lottery

Simple Christmas lottery app we use in my family. Key features:
- each user buys a gift for someone else every year.
- each user has a wishlist where they can note what they want and how much they want it.
- every change on a wishlist triggers an e-mail to assigned santa.
- wishlist might be locked by santa so assigned recipient does not modify it after buying.

## Local development

Figure out what's your private IP address. Mine is `192.168.159.136`. Then: 

```
git clone https://github.com/hejcz/christmas-lottery-frontend
cd christmas-lottery-frontend
npm install
node_modules/@angular/cli/bin/ng serve --host 192.168.159.136 --disableHostCheck

# Run backend from idea

sed -i 's/192.168.159.134/192.168.159.136/g' nginx-dev.conf 
docker-compose up

firefox localhost:9000
``` 