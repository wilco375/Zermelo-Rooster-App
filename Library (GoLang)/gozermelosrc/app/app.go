// Package gozer provides a sane backend for using Zermelo.
// It takes away the need to do JSON parsing, as it works with simple
// URL schemes and plain text.
//
// The URL scheme for getting one's schedule is
//	/r/organization/student
// or:
//	/r/organization/student/offset
// Gozer always returns the schedule of an entire day, with
// the optional 'offset' standing for 'after how many days':
// An offset of 0 is today; an offset of 1 is tomorrow; an offset of -1 is yesterday.
//
// Examples:
// To get the schedule of student 1004443 in Tabor College, with auth token 'abc':
//	/r/tabor/1004443/?t=abc
// To get tomorrow's schedule of student 632 in OSG College, with auth token '123':
//	/r/osg/632/1/?t=123
//
// Authorization:
// The path
//	/auth/organization/code
// will return the access token, usable for retrieving schedules,
// but if the code is already used, the response will be empty.
//
package gozer

import (
	"net/http"
	"strconv"
	"strings"
	"time"

	"google.golang.org/appengine"
	"google.golang.org/appengine/urlfetch"

	"github.com/mehlon/gozer/zapi"
)

func init() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Add("Access-Control-Allow-Origin", "*")
		http.ServeFile(w, r, r.URL.Path[1:])
	})

	http.HandleFunc("/r/", rHandler)
	http.HandleFunc("/auth/", authHandler)
}

func authHandler(w http.ResponseWriter, r *http.Request) {
	path := strings.Split(strings.TrimPrefix(r.URL.Path, "/auth/"), "/")
	if len(path) != 2 {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	org, code := path[0], path[1]

	a := zapi.New(org, "")
	c := urlfetch.Client(appengine.NewContext(r))
	if err := a.AuthClient(c, code); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	w.Write([]byte(a.Token))
}

func rHandler(w http.ResponseWriter, r *http.Request) {
	c := urlfetch.Client(appengine.NewContext(r))

	path := strings.Split(strings.TrimPrefix(r.URL.Path, "/r/"), "/")
	if len(path) < 2 {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	org, student := path[0], path[1]

	token := r.URL.Query().Get("t")
	if token == "" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	offset := "0"
	if len(path) >= 3 && path[2] != "" {
		offset = path[2]
	}

	offset1, err := strconv.Atoi(offset)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	day := time.Now().Add(time.Duration(offset1) * time.Hour * 24)

	s, err := zapi.New(org, token).ScheduleClient(c, student, day)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	w.Write(s.Bytes())
}
