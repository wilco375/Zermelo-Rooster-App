// Copyright (c) 2015, Dima Biletskyy | Edited by Wilco van Beijnum

package zapi

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/url"
	"strconv"
	"strings"
)

// Schedule represents all of the lessons on a given day.
type Schedule []Lesson

// String returns a human-readable string that looks like this:
//	1 du 305
//	2 en 104
//	3 mat 312
// That is, the time slot, the subject, and the location, each seperated by one space.
func (s Schedule) String() string { return string(s.Bytes()) }

// Bytes does the same as String, but returns a slice of bytes.
func (s Schedule) Bytes() []byte {
	if len(s) == 0 {
		return []byte{}
	}

	lessons := make([]string, 10)
	for _, v := range s {
		timeslot := v.StartTimeSlot
		var subject string
		var group string
		if(len(v.Subjects) != 0){
			subject = v.Subjects[0]
		}else{
			subject = "?"
		}
		
		if(len(v.Groups) != 0){
			group = v.Groups[0]
		}else{
			group = ""
		}
		
		start := v.Start
		end := v.End
		diffSec := end-start
		diffMin := diffSec/60
		
		var location string
		if len(v.Locations) != 0 {
			// My school prepends "w" to all locations.
			location = strings.TrimPrefix(v.Locations[0], "w")
		}else{
			location = "0"
		}
		if !v.Cancelled && !strings.Contains(lessons[timeslot]," "){
			if(diffMin < 45 && diffMin > 35){
				lessons[timeslot] = strconv.Itoa(timeslot) + "V " + subject + "-" + group + " " + location
			}else{
				lessons[timeslot] = strconv.Itoa(timeslot) + " " + subject + "-" + group + " " + location
			}
		}else if !v.Cancelled{
			if(diffMin < 45 && diffMin > 35){
				lessons[timeslot] = strconv.Itoa(timeslot) + "V " + subject + "/" + strings.Split(lessons[timeslot]," ")[1] + "-" + group + " " + location + "/" + strings.Split(lessons[timeslot]," ")[2]
			}else{
				lessons[timeslot] = strconv.Itoa(timeslot)  + " " + subject + "/" + strings.Split(lessons[timeslot]," ")[1] + "-" + group + " " + location + "/" + strings.Split(lessons[timeslot]," ")[2]
			}
		}else if !strings.Contains(lessons[timeslot]," "){
			if(diffMin < 45 && diffMin > 35){
				lessons[timeslot] = strconv.Itoa(timeslot) + "XV " + subject + "-" + group + " " + location
			}else{
				lessons[timeslot] = strconv.Itoa(timeslot) + "X " + subject + "-" + group + " " + location
			}
		}
	}

	var w bytes.Buffer
	for i := 1; i < len(lessons); i++ {
		w.WriteString(lessons[i])
		w.WriteByte('\n')

		// A break every 3 hours.
		if (i % 3) == 0 {
			w.WriteByte('\n')
		}
	}

	return w.Bytes()
}

// Lesson represents a single information about a lesson.
type Lesson struct {
	Subjects      []string
	Locations     []string
	Cancelled     bool
	StartTimeSlot int
	Start         int
	End           int
	Groups        []string
}

// An Agent is needed to do operations such as retrieving schedule.
type Agent struct {
	Api   string
	Token string
}

// New returns a new Agent.
// Token can be an empty string.
func New(organization, token string) *Agent {
	return &Agent{
		Api:   "https://" + organization + ".zportal.nl/api/v2/",
		Token: token,
	}
}

// Schedule returns the lessons the user has on the specified day.
func (a Agent) Schedule(user, start, end string) (Schedule, error) {
	return a.ScheduleClient(http.DefaultClient, user, start, end)
}

// ScheduleClient acts like Schedule but takes an extra argument which
// is the http.Client it should use to retrieve the schedule.
func (a Agent) ScheduleClient(c *http.Client, user , start, end string) (Schedule, error) {
	v := url.Values{}
	v.Set("start", start)
	v.Set("end", end)
	v.Set("valid", "true")
	v.Set("user", user)
	v.Set("fields", "subjects,cancelled,locations,startTimeSlot,start,end,groups")
	v.Set("access_token", a.Token)

	resp, err := c.Get(a.Api + "appointments" + "?" + v.Encode())
	if err != nil {
		return nil, err
	}

	var s struct {
		Response struct {
			Data []Lesson
		}
	}
	if err := json.NewDecoder(resp.Body).Decode(&s); err != nil {
		return nil, err
	}

	return s.Response.Data, nil
}
