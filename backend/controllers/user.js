const async = require('async');
const crypto = require('crypto');
const nodemailer = require('nodemailer');
const passport = require('passport');
const User = require('../models/User');

/**
 * GET /login
 * Login page.
 */
exports.getLogin = (req, res) => {
    if (req.user) {
        return res.redirect('/');
    }
    res.render('account/login', {
        title: 'Log in'
    });
};

/**
 * POST /login
 * Sign in using email and password.
 */
exports.postLogin = (req, res, next) => {
    req.assert('email', 'Email is not valid.').isEmail();
    req.assert('password', 'Password cannot be blank.').notEmpty();
    req.sanitize('email').normalizeEmail({ remove_dots: false });

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('/login');
    }

    passport.authenticate('local', (err, user, info) => {
        if (err) { return next(err); }
        if (!user) {
            req.flash('errors', info);
            return res.redirect('/login');
        }
        req.logIn(user, (err) => {
            if (err) { return next(err); }
            req.flash('success', { msg: 'Success! You are logged in.' });
            res.redirect(req.session.returnTo || '/');
        });
    })(req, res, next);
};

/**
 * GET /logout
 * Log out.
 */
exports.logout = (req, res) => {
    req.logout();
    res.redirect('/');
};

/**
 * GET /signup
 * Signup page.
 */
exports.getSignup = (req, res) => {
    if (req.user) {
        return res.redirect('/');
    }
    res.render('account/signup', {
        title: 'Create Account'
    });
};

/**
 * POST /signup
 * Create a new local account.
 */
exports.postSignup = (req, res, next) => {
    req.assert('email', 'Email is not valid.').isEmail();
    req.assert('password', 'Password must be at least 4 characters long.').len(4);
    req.assert('confirmPassword', 'Passwords do not match.').equals(req.body.password);
    req.sanitize('email').normalizeEmail({ remove_dots: false });

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('/signup');
    }

    const user = new User({
        profile: {
            firstName: req.body.firstName,
            lastName: req.body.lastName
        },
        email: req.body.email,
        password: req.body.password
    });

    User.findOne({ email: req.body.email }, (err, existingUser) => {
        if (existingUser) {
            req.flash('errors', { msg: 'Account with that email address already exists.' });
            return res.redirect('/signup');
        }
        user.save((err) => {
            if (err) { return next(err); }
            req.logIn(user, (err) => {
                if (err) {
                    return next(err);
                }
                res.redirect('/');
            });
        });
    });
};

/**
 * GET /account
 * Profile page.
 */
exports.getAccount = (req, res) => {
    res.render('account/profile', {
        title: 'Account Management'
    });
};

/**
 * POST /account/profile
 * Update profile information.
 */
exports.postUpdateProfile = (req, res, next) => {
    req.assert('email', 'Please enter a valid email address.').isEmail();
    req.assert('website', 'Please enter a valid URL.').optional({ checkFalsy: true }).isURL();
    req.sanitize('email').normalizeEmail({ remove_dots: false });

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('/account');
    }

    User.findById(req.user.id, (err, user) => {
        if (err) { return next(err); }
        user.email = req.body.email || '';
        user.profile.firstName = req.body.firstName || '';
        user.profile.lastName = req.body.lastName || '';
        user.profile.gender = req.body.gender || '';
        user.profile.location = req.body.location || '';
        user.profile.website = req.body.website || '';
        user.save((err) => {
            if (err) {
                if (err.code === 11000) {
                    req.flash('errors', { msg: 'The email address you have entered is already associated with an account.' });
                    return res.redirect('/account');
                }
                return next(err);
            }
            req.flash('success', { msg: 'Profile information has been updated.' });
            res.redirect('/account');
        });
    });
};

/**
 * POST /account/password
 * Update current password.
 */
exports.postUpdatePassword = (req, res, next) => {
    req.assert('password', 'Password must be at least 4 characters long.').len(4);
    req.assert('confirmPassword', 'Passwords do not match.').equals(req.body.password);

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('/account');
    }

    User.findById(req.user.id, (err, user) => {
        if (err) { return next(err); }
        user.password = req.body.password;
        user.save((err) => {
            if (err) { return next(err); }
            req.flash('success', { msg: 'Password has been changed.' });
            res.redirect('/account');
        });
    });
};

/**
 * POST /account/delete
 * Delete user account.
 */
exports.postDeleteAccount = (req, res, next) => {
    User.remove({ _id: req.user.id }, (err) => {
        if (err) { return next(err); }
        req.logout();
        req.flash('info', { msg: 'Your account has been deleted.' });
        res.redirect('/');
    });
};

/**
 * GET /account/unlink/:provider
 * Unlink OAuth provider.
 */
exports.getOauthUnlink = (req, res, next) => {
    const provider = req.params.provider;
    User.findById(req.user.id, (err, user) => {
        if (err) { return next(err); }
        user[provider] = undefined;
        user.tokens = user.tokens.filter(token => token.kind !== provider);
		const providerFormatted = provider.charAt(0).toUpperCase() + provider.slice(1);
        user.save((err) => {
            if (err) { return next(err); }
            req.flash('info', { msg: `${providerFormatted} account has been unlinked.` });
            res.redirect('/account');
        });
    });
};

/**
 * GET /reset/:token
 * Reset Password page.
 */
exports.getReset = (req, res, next) => {
    if (req.isAuthenticated()) {
        return res.redirect('/');
    }
    User
        .findOne({ passwordResetToken: req.params.token })
        .where('passwordResetExpires').gt(Date.now())
        .exec((err, user) => {
            if (err) { return next(err); }
            if (!user) {
                req.flash('errors', { msg: 'Password reset token is invalid or has expired.' });
                return res.redirect('/forgot');
            }
            res.render('account/reset', {
                title: 'Password Reset'
            });
        });
};

/**
 * POST /reset/:token
 * Process the reset password request.
 */
exports.postReset = (req, res, next) => {
    req.assert('password', 'Password must be at least 4 characters long.').len(4);
    req.assert('confirm', 'Passwords must match.').equals(req.body.password);

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('back');
    }

    async.waterfall([
        function (done) {
            User
                .findOne({ passwordResetToken: req.params.token })
                .where('passwordResetExpires').gt(Date.now())
                .exec((err, user) => {
                    if (err) { return next(err); }
                    if (!user) {
                        req.flash('errors', { msg: 'Password reset token is invalid or has expired.' });
                        return res.redirect('back');
                    }
                    user.password = req.body.password;
                    user.passwordResetToken = undefined;
                    user.passwordResetExpires = undefined;
                    user.save((err) => {
                        if (err) { return next(err); }
                        req.logIn(user, (err) => {
                            done(err, user);
                        });
                    });
                });
        },
        function (user, done) {
            const transporter = nodemailer.createTransport({
                service: 'SendGrid',
                auth: {
                    user: process.env.SENDGRID_USER,
                    pass: process.env.SENDGRID_PASSWORD
                }
            });
            const mailOptions = {
                to: user.email,
                from: 'hello@sinless.com',
                subject: 'Your SinLess password has been changed',
                text: `Hello,\n\nThis is a confirmation that the password for your account ${user.email} has just been changed.\n`
            };
            transporter.sendMail(mailOptions, (err) => {
                req.flash('success', { msg: 'Success! Your password has been changed.' });
                done(err);
            });
        }
    ], (err) => {
        if (err) { return next(err); }
        res.redirect('/');
    });
};

/**
 * GET /forgot
 * Forgot Password page.
 */
exports.getForgot = (req, res) => {
    if (req.isAuthenticated()) {
        return res.redirect('/');
    }
    res.render('account/forgot', {
        title: 'Forgot Password'
    });
};

/**
 * POST /forgot
 * Create a random token, then the send user an email with a reset link.
 */
exports.postForgot = (req, res, next) => {
    req.assert('email', 'Please enter a valid email address.').isEmail();
    req.sanitize('email').normalizeEmail({ remove_dots: false });

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('/forgot');
    }

    async.waterfall([
        function (done) {
            crypto.randomBytes(16, (err, buf) => {
                const token = buf.toString('hex');
                done(err, token);
            });
        },
        function (token, done) {
            User.findOne({ email: req.body.email }, (err, user) => {
                if (!user) {
                    req.flash('errors', { msg: 'Account with that email address does not exist.' });
                    return res.redirect('/forgot');
                }
                user.passwordResetToken = token;
                user.passwordResetExpires = Date.now() + 3600000; // 1 hour
                user.save((err) => {
                    done(err, token, user);
                });
            });
        },
        function (token, user, done) {
            const transporter = nodemailer.createTransport({
                service: 'SendGrid',
                auth: {
                    user: process.env.SENDGRID_USER,
                    pass: process.env.SENDGRID_PASSWORD
                }
            });
            const mailOptions = {
                to: user.email,
                from: 'hello@sinless.com',
                subject: 'Reset your password on SinLess',
                text: `You are receiving this email because you (or someone else) have requested the reset of the password for your account.\n\n
                    Please click on the following link, or paste this into your browser to complete the process:\n\n
                    http://${req.headers.host}/reset/${token}\n\n
                    If you did not request this, please ignore this email and your password will remain unchanged.\n`
            };
            transporter.sendMail(mailOptions, (err) => {
                req.flash('info', { msg: `An e-mail has been sent to ${user.email} with further instructions.` });
                done(err);
            });
        }
    ], (err) => {
        if (err) { return next(err); }
        res.redirect('/forgot');
    });
};

/**
 * GET /events
 * Events page.
 */
exports.getEvents = (req, res) => {
    res.render('events/home', {
        title: 'Events'
    });
};

/**
 * GET /groups
 * Groups page.
 */
exports.getGroups = (req, res) => {
    res.render('groups/home', {
        title: 'Groups'
    });
};

/**
 * GET /events/create
 * Event creation page.
 */
exports.getEventsCreate = (req, res) => {
    res.render('events/create', {
        title: 'Create event'
    });
};

/**
 * GET /groups/create
 * Group creation page.
 */
exports.getGroupsCreate = (req, res) => {
    res.render('groups/create', {
        title: 'Create group'
    });
};

/**
 * POST /events/create
 * Create a new event.
 */
exports.postEventsCreate = (req, res, next) => {
    req.assert('description', 'The description must be less than 140 characters.').len(0, 140);
    req.assert('guests', 'You must add at least one guest to this event.').isArray();

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('/events/create');
    }

    const event = new Event({
        name: req.body.name,
        description: req.body.description || '',
        location: req.body.location,
        times: [{time: Date.now(), value: true}],
        owner: req.user._id,
        planners: [req.user._id],
        guests: [req.user._id]
    });

    event.save((err) => {
        if (err) { return next(err); }
        console.log("test");
        User.findByIdAndUpdate(req.user._id, {
            $push: { events: { name: event.name, id: event._id } }
        }, { 'new': true}, (err) => {
            if (err) { return next(err); }
            return res.redirect('/events/' + event._id);
        });
    });
};

/**
 * POST /groups/create
 * Create a new group.
 */
exports.postGroupsCreate = (req, res, next) => {
    req.assert('description', 'The description must be less than 140 characters.').len(0, 140);
    req.check('members', 'You must add at least one member to this group.').isArray();

    const errors = req.validationErrors();

    if (errors) {
        req.flash('errors', errors);
        return res.redirect('/groups/create');
    }

    const group = new Group({
        name: req.body.name,
        description: req.body.description || '',
        owner: req.user._id,
        managers: [req.user._id],
        members: [req.user._id]
    });

    group.save((err) => {
        if (err) { return next(err); }
        User.findByIdAndUpdate(req.user._id, {
            $push: { groups: { name: group.name, id: group._id } }
        }, { 'new': true}, (err) => {
            if (err) { return next(err); }
            return res.redirect('/groups/' + group._id);
        });
    });
};

/**
 * GET /events/:id
 * Event page.
 */
exports.getEvent = (req, res) => {
    Event.findById(req.params.id, (err, event) => {
        if (err) { return next(err); }
        res.render('events/event', {
            title: 'Event',
            event: event
        });
    });
};

/**
 * GET /groups/:id
 * Group page.
 */
exports.getGroup = (req, res, next) => {
    Group.findById(req.params.id, (err, group) => {
        if (err) { return next(err); }
        res.render('groups/group', {
            title: 'Group',
            group: group
        });
    });
};

/**
 * POST /events/:id
 * Update event information.
 */
exports.postEvent = (req, res) => {
    Event.findById(req.params.id, (err, event) => {
        if (err) { return next(err); }
        event.name = req.body.name;
        event.description = req.body.description || '';
        event.owner = req.user._id;
        event.managers = [req.user._id];
        event.members = [req.user._id];
        event.save((err) => {
            if (err) { return next(err); }
            req.flash('success', { msg: 'Event information has been updated.' });
            res.render('events/event', {
                title: 'Event',
                event: event
            });
        });
    });
};

/**
 * POST /groups/:id
 * Update group information.
 */
exports.postGroup = (req, res) => {
    Group.findById(req.params.id, (err, group) => {
        if (err) { return next(err); }
        group.name = req.body.name;
        group.description = req.body.description || '';
        group.owner = req.user._id;
        group.managers = [req.user._id];
        group.members = [req.user._id];
        group.save((err) => {
            if (err) { return next(err); }
            req.flash('success', { msg: 'Group information has been updated.' });
            res.render('groups/group', {
                title: 'Group',
                group: group
            });
        });
    });
};

/**
 * POST /events/:id/delete
 * Delete event page.
 */
exports.deleteEvent = (req, res) => {
    const id = req.params.id;
    Event.remove({ _id: id }, (err) => {
        if (err) { return next(err); }
        req.flash('info', { msg: 'The event has been deleted.' });
        User.update({ _id: req.user._id }, { $pull: { events: { id: id } } }, (err) => {
            if (err) { return next(err); }
            return res.redirect('/events');
        });
    });
};

/**
 * POST /groups/:id/delete
 * Delete group page.
 */
exports.deleteGroup = (req, res, next) => {
    const id = req.params.id;
    Group.remove({ _id: req.params.id }, (err) => {
        if (err) { return next(err); }
        req.flash('info', { msg: 'The group has been deleted.' });
        User.update({ _id: req.user._id }, { $pull: { groups: { id: id } } }, (err) => {
            if (err) { return next(err); }
            return res.redirect('/groups');
        });
    });
};
