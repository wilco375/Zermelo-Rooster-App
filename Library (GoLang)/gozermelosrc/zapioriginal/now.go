package zapioriginal

import "time"

type Now struct {
	time.Time
}

func (now *Now) BeginningOfDay() time.Time {
	d := time.Duration(-now.Hour()) * time.Hour
	return now.BeginningOfHour().Add(d)
}
func (now *Now) EndOfDay() time.Time {
	return now.BeginningOfDay().Add(24*time.Hour - time.Nanosecond)
}
func (now *Now) BeginningOfHour() time.Time {
	return now.Truncate(time.Hour)
}
