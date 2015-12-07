// Copyright (c) 2015, Dima Biletskyy | Edited by Wilco van Beijnum
// Package gozermelo can be used with gobind (https://godoc.org/golang.org/x/mobile/cmd/gobind).

package zermelogo

import (
	"gozermelosrc/zapi"
	"gozermelosrc/zapioriginal"
)

func Auth(org, code string) string {
	z := zapi.New(org, "")
	if err := z.Auth(code); err != nil {
		return ""
	}
	return z.Token
}

func GetScheduleByTime(org, token , start , end string) string {
	lessons, err := zapi.New(org, token).Schedule("~me", start, end)
	
	if err != nil {
		return ""
	}

	return lessons.String()
}

func GetScheduleByTimeInJson(org, token , start , end string) string {
	lessons, err := zapioriginal.New(org, token).Schedule("~me", start, end)
	
	if err != nil {
		return ""
	}

	return lessons
}