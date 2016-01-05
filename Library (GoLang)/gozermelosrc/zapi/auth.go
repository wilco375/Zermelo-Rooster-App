package zapi

import (
	"encoding/json"
	"net/http"
	"net/url"
)

// Auth authorizes the agent using the code.
func (a *Agent) Auth(code string) error {
	return a.AuthClient(http.DefaultClient, code)
}

// AuthClient behaves like Auth but takes an additional parameter
// specifying the HTTP client to use.
func (a *Agent) AuthClient(c *http.Client, code string) error {
	v := url.Values{}
	v.Set("grant_type", "authorization_code")
	v.Set("code", code)

	resp, err := c.PostForm(a.Api+"oauth/token", v)
	if err != nil {
		return err
	}

	var s struct {
		AccessToken string `json:"access_token"`
	}
	if err := json.NewDecoder(resp.Body).Decode(&s); err != nil {
		return err
	}

	a.Token = s.AccessToken

	return nil
}
