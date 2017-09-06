from flask import Flask, jsonify, request
from flask_pymongo import PyMongo
from werkzeug.security import generate_password_hash, \
     check_password_hash

app = Flask(__name__)
app.config['MONGO_DBNAME'] = 'final'
app.config['MONGO_URI'] = 'mongodb://localhost:27017/final'

mongo = PyMongo(app)

@app.route('/login', methods=['POST'])
def get_all_users():
    user = mongo.db.users
    existing_user = user.find_one({"name": request.json['name']})
    if(existing_user):
        entered_pass = request.json['password']
        db_pass = existing_user['password']
        pass_check = check_password_hash(db_pass, entered_pass)
        if(pass_check):
            return jsonify({'code':'login_success'})
    return jsonify({'code':'login_fail'})


@app.route('/register', methods=['POST'])
def add_user():
    user = mongo.db.users
    existing_user = user.find_one({"name": request.json['name']})
    if(existing_user is None):
        name = request.json['name']
        hashed_pass = generate_password_hash(request.json['password'])
        user.insert({'name':name, 'password':hashed_pass})
        return jsonify({'code': 'success'})
    return jsonify({'code': 'user_already_exists'})


if __name__ == '__main__':
    app.run(debug=True)
