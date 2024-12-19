package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"time"

	"github.com/segmentio/kafka-go"
)

type Message struct {
	ID        string    `json:"id"`
	Content   string    `json:"content"`
	Timestamp time.Time `json:"timestamp"`
}

func main() {
	// กำหนดค่าเริ่มต้น
	broker := "localhost:9092"
	topic := "test-topic"

	// ตัวอย่างการใช้ Producer
	go runProducer(broker, topic)

	// ตัวอย่างการใช้ Consumer
	go runConsumerA(broker, topic)
	go runConsumerB(broker, topic)

	// รอให้โปรแกรมทำงานสักพัก
	time.Sleep(1 * time.Minute)
}

func runProducer(broker, topic string) {
	writer := kafka.NewWriter(kafka.WriterConfig{
		Brokers: []string{broker},
		Topic:   topic,
	})
	defer writer.Close()

	// ส่งข้อมูลทุกๆ 2 วินาที
	for i := 0; ; i++ {
		msg := Message{
			ID:        fmt.Sprintf("msg-%d", i),
			Content:   fmt.Sprintf("This is message #%d", i),
			Timestamp: time.Now(),
		}

		// แปลง struct เป็น JSON
		value, err := json.Marshal(msg)
		if err != nil {
			log.Printf("failed to marshal message: %v", err)
			continue
		}

		err = writer.WriteMessages(context.Background(),
			kafka.Message{
				Key:   []byte(fmt.Sprintf("key-%d", i)),
				Value: value,
			},
		)

		if err != nil {
			log.Printf("failed to write message: %v", err)
			continue
		}

		log.Printf("produced message: %s", value)
		time.Sleep(2 * time.Second)
	}
}

func runConsumerA(broker, topic string) {
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{broker},
		Topic:   topic,
		GroupID: "my-group",
	})
	defer reader.Close()

	for {
		m, err := reader.ReadMessage(context.Background())
		if err != nil {
			log.Printf("error reading message: %v", err)
			continue
		}

		var msg Message
		if err := json.Unmarshal(m.Value, &msg); err != nil {
			log.Printf("error unmarshaling message: %v", err)
			continue
		}

		log.Printf("A received message: %+v", msg)

		time.Sleep(2 * time.Second)
	}
}

func runConsumerB(broker, topic string) {
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{broker},
		Topic:   topic,
		GroupID: "my-group2",
	})
	defer reader.Close()

	for {
		m, err := reader.ReadMessage(context.Background())
		if err != nil {
			log.Printf("error reading message: %v", err)
			continue
		}

		var msg Message
		if err := json.Unmarshal(m.Value, &msg); err != nil {
			log.Printf("error unmarshaling message: %v", err)
			continue
		}

		log.Printf("B received message: %+v", msg)

		time.Sleep(2 * time.Second)
	}
}
