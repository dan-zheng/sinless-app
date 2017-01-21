const bcrypt = require('bcrypt-nodejs');
const crypto = require('crypto');
const identicon = require('identicon.js');
const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    email: { type: String, unique: true },
    password: String,
    passwordResetToken: String,
    passwordResetExpires: Date,

    facebook: String,
    google: String,
    github: String,
    tokens: Array,

    profile: {
        firstName: { type: String, default: '' },
        lastName: { type: String, default: '' },
        location: { type: String, default: '' },
        website: { type: String, default: '' },
        picture: { type: String, default: '' }
    },
}, { timestamps: true });

/**
 * Password hash middleware.
 */
userSchema.pre('save', function (next) {
    const user = this;
    if (!user.isModified('password')) { return next(); }
    bcrypt.genSalt(10, (err, salt) => {
        if (err) { return next(err); }
        bcrypt.hash(user.password, salt, null, (err, hash) => {
            if (err) { return next(err); }
            user.password = hash;
            next();
        });
    });
});

/**
 * Helper method for validating user's password.
 */
userSchema.methods.comparePassword = function (candidatePassword, cb) {
    bcrypt.compare(candidatePassword, this.password, (err, isMatch) => {
        cb(err, isMatch);
    });
};

/**
 * Helper method for getting user's identicon.
 */
userSchema.methods.identicon = function (size) {
    if (!size) {
        size = 200;
    }
    if (!this.email) {
        const data = new identicon().toString();
        return `data:image/png;base64,` + data;
    }
    const md5 = crypto.createHash('md5').update(this.email).digest('hex');
    const data = new identicon(md5, 420).toString();
    return `data:image/png;base64,` + data;
};

const User = mongoose.model('User', userSchema);

module.exports = User;
