# Christmas Lottery

This is a backend for a Christmas lottery app we use in my family. Rules it follows:

- each family member buys a gift for someone else every year. If person A gifted B 2 times and C 3 times an algorithm
  shold prefer B over C.
- each user has a wishlist where they can note what they want and order their wishes from the least to the most
  important.
- every change on a wishlist triggers an e-mail to assigned santa.
- wishlist might be locked by santa so assigned recipient does not modify it after buying.
- there must an option to forbid certain matches e.g. wife and husband.

## Deployment

One of the goals of this project was to host it without expenses,
so right now the setup is:

### node and java version

```bash
nvm use 10.16.3
sdk use java 21.0.1-tem
```

### Local app

```bash
./clean_deploy.sh

## or if you already did clean deploy once

./fast_build.sh
```

### Local postgres docker container

```bash
docker run --name lottery-pg -e POSTGRES_USER=santa -e POSTGRES_PASSWORD=example -e POSTGRES_DB=p1030_loteria -p 8543:5432 -d postgres
```

### ngrok

```bash
docker run -it -e NGROK_AUTHTOKEN=<TOKEN> --net=host ngrok/ngrok http 8080 --domain=<DOMAIN>
```

### Brevo

For sending e-mails.
https://www.brevo.com/
