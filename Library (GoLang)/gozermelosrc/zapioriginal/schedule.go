// Copyright (c) 2015, Dima Biletskyy | Edited by Wilco van Beijnum

package zapioriginal

import (
	"net/http"
	"net/url"	
    "io/ioutil"
)

// Schedule represents all of the lessons on a given day.
type Schedule []Lesson

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
func (a Agent) Schedule(user, start, end string) (string, error) {
	return a.ScheduleClient(http.DefaultClient, user, start, end)
}

// ScheduleClient acts like Schedule but takes an extra argument which
// is the http.Client it should use to retrieve the schedule.
func (a Agent) ScheduleClient(c *http.Client, user , start, end string) (string, error) {
	v := url.Values{}
	v.Set("start", start)
	v.Set("end", end)
	v.Set("valid", "true")
	v.Set("user", user)
	v.Set("fields", "subjects,cancelled,locations,startTimeSlot,start,end,groups")
	v.Set("access_token", a.Token)

	resp, err := c.Get(a.Api + "appointments" + "?" + v.Encode())
	
	if err != nil {
		return "",err
	}else{
		defer resp.Body.Close()
		contents, err := ioutil.ReadAll(resp.Body)
		if err != nil {
			return "",err
		}
		return string(contents),nil
	}
}
