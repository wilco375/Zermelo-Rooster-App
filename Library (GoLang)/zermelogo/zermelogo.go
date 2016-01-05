// Package gozermelo can be used with gobind (https://godoc.org/golang.org/x/mobile/cmd/gobind).
package zermelogo

import (
	"gozermelosrc/zapi"
)

func Auth(org, code string) string {
	z := zapi.New(org, "")
	if err := z.Auth(code); err != nil {
		return ""
	}
	return z.Token
}

func GetScheduleByTimeInJson(org, token , start , end string) string {
	lessons, err := zapi.New(org, token).Schedule("~me", start, end)
	
	if err != nil {
		return ""
	}

	return lessons
}