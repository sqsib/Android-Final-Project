import requests

r = requests.post("http://127.0.0.1:5000/test2", data={"name": "try2"})
print(r.status_code, r.reason)
