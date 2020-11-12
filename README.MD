# Meet your santa

Simple web app to run a christmas lottery that:
- ensures that every year you buy present to someone else. 
- allows you to specify WHAT you want to receive and how MUCH you want it.
- e-mails you every time your match changes their wishlist.

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